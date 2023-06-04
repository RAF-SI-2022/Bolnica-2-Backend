package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.request.TestingRequest;
import com.raf.si.patientservice.dto.response.AvailableTermResponse;
import com.raf.si.patientservice.dto.response.ScheduledTestingListResponse;
import com.raf.si.patientservice.dto.response.ScheduledTestingResponse;
import com.raf.si.patientservice.dto.response.TestingResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.TestingMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.PatientConditionRepository;
import com.raf.si.patientservice.repository.ScheduledTestingRepository;
import com.raf.si.patientservice.repository.TestingRepository;
import com.raf.si.patientservice.repository.filtering.filter.ScheduledTestingFilter;
import com.raf.si.patientservice.repository.filtering.specification.ScheduledTestingSpecification;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.TestingService;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class TestingServiceImpl implements TestingService {

    private final ScheduledTestingRepository scheduledTestingRepository;
    private final TestingRepository testingRepository;
    private final AvailableTermRepository availableTermRepository;
    private final PatientConditionRepository patientConditionRepository;
    private final PatientService patientService;
    private final TestingMapper testingMapper;
    private final LockRegistry lockRegistry;

    public TestingServiceImpl(ScheduledTestingRepository scheduledTestingRepository,
                              TestingRepository testingRepository,
                              AvailableTermRepository availableTermRepository,
                              PatientConditionRepository patientConditionRepository,
                              PatientService patientService,
                              TestingMapper testingMapper,
                              LockRegistry lockRegistry) {

        this.scheduledTestingRepository = scheduledTestingRepository;
        this.testingRepository = testingRepository;
        this.availableTermRepository = availableTermRepository;
        this.patientConditionRepository = patientConditionRepository;
        this.patientService = patientService;
        this.testingMapper = testingMapper;
        this.lockRegistry = lockRegistry;
    }

    @Override
    public ScheduledTestingResponse scheduleTesting(UUID lbp, ScheduledTestingRequest request, String token) {
        LocalDateTime day =request.getDateAndTime().truncatedTo(ChronoUnit.DAYS);
        Lock dayLock = lockRegistry.obtain(day.toString());
        Lock patientLock = lockRegistry.obtain(String.valueOf(lbp));
        try{
            if(!dayLock.tryLock(1, TimeUnit.SECONDS) || !patientLock.tryLock(1, TimeUnit.SECONDS)){
                String errMessage = "Neko već pokušava da ubaci pregled za prosledjeni dan ili za pacijenta";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
            log.info(String.format("Locking for patient %s and day %s", lbp, day));
        } catch (BadRequestException e) {
            throw e;
        }catch(InterruptedException e){
            log.info(e.getMessage());
            throw new InternalServerErrorException("Greška pri zaključavanju baze");
        }

        ScheduledTestingResponse response;
        try{
            response = scheduleTestingLocked(lbp, request, token);
        }catch(RuntimeException e) {
            throw e;
        }finally{
            dayLock.unlock();
            patientLock.unlock();
            log.info(String.format("Unlocking for patient %s and day %s", lbp, day));
        }
        return response;
    }

    private ScheduledTestingResponse scheduleTestingLocked(UUID lbp,
                                                           ScheduledTestingRequest request,
                                                           String token) {

        LocalDateTime requestDate = request.getDateAndTime().truncatedTo(ChronoUnit.SECONDS);
        request.setDateAndTime(requestDate);

        Patient patient = patientService.findPatient(lbp);
        ScheduledTesting scheduledTesting = testingMapper.scheduledTestingRequestToModel(patient, request);
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        AvailableTerm availableTerm;

        checkDateInFuture(request.getDateAndTime());
        checkPatientTestingsForDay(patient, request.getDateAndTime());

        LocalDateTime endDateAndTime = request.getDateAndTime().plusMinutes(ScheduledTesting.getTestDurationMinutes());
        List<AvailableTerm> availableTerms = availableTermRepository.findByDateAndTimeBetweenAndPbo(
                request.getDateAndTime(),
                endDateAndTime,
                tokenPayload.getPbo()
        );

        if (availableTerms != null && availableTerms.size() > 0) {
            availableTerm = checkAndGetAvailableTerm(availableTerms);
        } else {
            availableTerm = makeAvailableTerm(request.getDateAndTime(), token);
        }

        availableTerm.incrementScheduledTermsNum();
        if (availableTerm.getScheduledTermsNum() == availableTerm.getAvailableNursesNum()) {
            availableTerm.setAvailability(Availability.POTPUNO_POPUNJEN_TERMIN);
        }
        availableTerm = availableTermRepository.save(availableTerm);

        scheduledTesting.setAvailableTerm(availableTerm);
        scheduledTesting = scheduledTestingRepository.save(scheduledTesting);

        log.info(String.format("Kreirano novo zakazano testiranje za pacijenta sa lbp-om %s, broj slobodnih termina za termin %s je %d",
                patient.getLbp(),
                availableTerm.getDateAndTime().toString(),
                (availableTerm.getAvailableNursesNum() - availableTerm.getScheduledTermsNum())));
        return testingMapper.scheduledTestingToResponse(scheduledTesting);
    }

    @Override
    public AvailableTermResponse getAvailableTerm(LocalDateTime dateAndTime, String token) {
        dateAndTime = dateAndTime.truncatedTo(ChronoUnit.SECONDS);
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        Optional<AvailableTerm> availableTermOptional = availableTermRepository.findByDateAndTimeAndPbo(dateAndTime, tokenPayload.getPbo());
        AvailableTerm availableTerm;

        if (availableTermOptional.isEmpty()) {
            availableTerm = makeAvailableTerm(dateAndTime, token);
        } else {
            availableTerm = availableTermOptional.get();
        }

        return testingMapper.availableTermToResponse(availableTerm);
    }

    @Override
    public ScheduledTestingListResponse getScheduledtestings(UUID lbp, LocalDate date, Pageable pageable) {
        Patient patient = lbp == null? null: patientService.findPatient(lbp);
        LocalDateTime dateTime = date.atStartOfDay();

        ScheduledTestingFilter filter = new ScheduledTestingFilter(patient, dateTime);
        ScheduledTestingSpecification spec = new ScheduledTestingSpecification(filter);

        Page<ScheduledTesting> scheduledTestingPage = scheduledTestingRepository.findAll(spec, pageable);
        return testingMapper.scheduledTestingPageToResponse(scheduledTestingPage);
    }

    @Override
    public TestingResponse createTesting(UUID lbp, TestingRequest request) {
        Patient patient = patientService.findPatient(lbp);
        Testing testing = testingMapper.testingRequestToTesting(patient, request);
        PatientCondition patientCondition = testingMapper.testingRequestToPatientCondition(patient, request);

        checkDateInPast(request.getCollectedInfoDate());

        patientCondition = patientConditionRepository.save(patientCondition);

        testing.setPatientCondition(patientCondition);
        testing = testingRepository.save(testing);

        log.info(String.format("Kreirano testiranje u terminu %s za pacijenta sa lbp-om %s",
                testing.getDateAndTime().toString(),
                patient.getLbp()));
        return testingMapper.testingToResponse(testing, patientCondition);
    }

    private void checkDateInFuture(LocalDateTime date){
        LocalDateTime currDate = LocalDateTime.now();
        if (currDate.isAfter(date)) {
            String errMessage = String.format("Datum %s je u proslosti", date);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void checkDateInPast(Date date) {
        Date currDate = new Date();
        if (currDate.before(date)) {
            String errMessage = String.format("Datum %s je u buducnosti", date);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void checkPatientTestingsForDay(Patient patient, LocalDateTime dateAndTime) {
        LocalDateTime startDate = dateAndTime.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endDate = startDate.plusDays(1);

        List<ScheduledTesting> tests = scheduledTestingRepository.findByPatientAndDateAndTimeBetween(patient, startDate, endDate);
        if (tests != null && tests.size() > 0) {
            String errMessage = String.format("Pacijent sa lbp-om %s vec ima zakazano testiranje za prosledjeni dan", patient.getLbp());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private AvailableTerm checkAndGetAvailableTerm(List<AvailableTerm> availableTerms) {
        if (availableTerms.size() > 1) {
            String errMessage = "Postoji vise termina zakazanih za trazeno vreme";
            log.error(errMessage);
            throw new InternalServerErrorException(errMessage);
        }

        AvailableTerm term = availableTerms.get(0);
        if (term.getAvailability() == Availability.POTPUNO_POPUNJEN_TERMIN) {
            String errMessage = "Termin potpuno popunjen";
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        return term;
    }

    private AvailableTerm makeAvailableTerm(LocalDateTime dateAndTime, String token) {
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        int availableNurses = getAvailableNurses(tokenPayload.getPbo(), token);
        return testingMapper.makeAvailableTerm(dateAndTime,
                tokenPayload.getPbo(),
                availableNurses);
    }

    private int getAvailableNurses(UUID pbo, String token) {
        int availableNurses;
        try {
            availableNurses = HttpUtils.getNumOfCovidNursesForDepartment(pbo, token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }

        if (availableNurses < 1) {
            String errMessage = String.format("Nema dostupnih sestara za departman sa pbo-om %s", pbo);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
        return availableNurses;
    }
}
