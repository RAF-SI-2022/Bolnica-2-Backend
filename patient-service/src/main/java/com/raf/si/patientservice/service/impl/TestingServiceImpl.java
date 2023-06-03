package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.dto.response.ScheduledTestingResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.TestingMapper;
import com.raf.si.patientservice.model.AvailableTerm;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledTesting;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.ScheduledTestingRepository;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.TestingService;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class TestingServiceImpl implements TestingService {

    private final ScheduledTestingRepository scheduledTestingRepository;
    private final AvailableTermRepository availableTermRepository;
    private final PatientService patientService;
    private final TestingMapper testingMapper;
    private final LockRegistry lockRegistry;

    public TestingServiceImpl(ScheduledTestingRepository scheduledTestingRepository,
                              AvailableTermRepository availableTermRepository,
                              PatientService patientService,
                              TestingMapper testingMapper,
                              LockRegistry lockRegistry) {

        this.scheduledTestingRepository = scheduledTestingRepository;
        this.availableTermRepository = availableTermRepository;
        this.patientService = patientService;
        this.testingMapper = testingMapper;
        this.lockRegistry = lockRegistry;
    }

    @Override
    public ScheduledTestingResponse scheduleTesting(UUID lbp, ScheduledTestingRequest request, String token) {
        Date day = DateUtils.truncate(request.getDateAndTime(), Calendar.DAY_OF_MONTH);
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
        Patient patient = patientService.findPatient(lbp);
        ScheduledTesting scheduledTesting = testingMapper.scheduledTestingRequestToModel(patient, request);
        AvailableTerm availableTerm;

        checkDate(request.getDateAndTime());
        checkPatientTestingsForDay(patient, request.getDateAndTime());

        Date endDateAndTime = DateUtils.addMinutes(request.getDateAndTime(), ScheduledTesting.getTestDurationMinutes());
        List<AvailableTerm> availableTerms = availableTermRepository.findByDateAndTimeBetween(
                request.getDateAndTime(),
                endDateAndTime
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
        availableTermRepository.save(availableTerm);

        scheduledTesting.setAvailableTerm(availableTerm);
        scheduledTestingRepository.save(scheduledTesting);

        return testingMapper.scheduledTestingToResponse(scheduledTesting);
    }

    private void checkDate(Date date){
        Date currDate = new Date();
        if (currDate.after(date)) {
            String errMessage = String.format("Datum %s je u proslosti", date.toString());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void checkPatientTestingsForDay(Patient patient, Date dateAndTime) {
        Date startDate = DateUtils.truncate(dateAndTime, Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.addDays(startDate, 1);

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

    private AvailableTerm makeAvailableTerm(Date dateAndTime, String token) {
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        int availableNurses;
        try {
            availableNurses = HttpUtils.getNumOfCovidNursesForDepartment(tokenPayload.getPbo(), token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }

        if (availableNurses < 1) {
            String errMessage = String.format("Nema dostupnih sestara za departman sa pbo-om %s", tokenPayload.getPbo());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        return testingMapper.makeAvailableTerm(dateAndTime,
                tokenPayload.getPbo(),
                availableNurses);
    }
}
