package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.request.TestingRequest;
import com.raf.si.patientservice.dto.request.TimeRequest;
import com.raf.si.patientservice.dto.request.UpdateTermsNewShiftRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.TestingMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.model.enums.testing.TestResult;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.PatientConditionRepository;
import com.raf.si.patientservice.repository.ScheduledTestingRepository;
import com.raf.si.patientservice.repository.TestingRepository;
import com.raf.si.patientservice.repository.filtering.filter.ScheduledTestingFilter;
import com.raf.si.patientservice.repository.filtering.specification.ScheduledTestingSpecification;
import com.raf.si.patientservice.service.CovidCertificateService;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.raf.si.patientservice.model.enums.examination.ExaminationStatus.OTKAZANO;
import static com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus.*;


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
    private final CovidCertificateService covidCertificateService;
    @PersistenceContext
    private EntityManager entityManager;

    public TestingServiceImpl(ScheduledTestingRepository scheduledTestingRepository,
                              TestingRepository testingRepository,
                              AvailableTermRepository availableTermRepository,
                              PatientConditionRepository patientConditionRepository,
                              PatientService patientService,
                              TestingMapper testingMapper,
                              LockRegistry lockRegistry, CovidCertificateService covidCertificateService) {

        this.scheduledTestingRepository = scheduledTestingRepository;
        this.testingRepository = testingRepository;
        this.availableTermRepository = availableTermRepository;
        this.patientConditionRepository = patientConditionRepository;
        this.patientService = patientService;
        this.testingMapper = testingMapper;
        this.lockRegistry = lockRegistry;
        this.covidCertificateService = covidCertificateService;
    }

    @Override
    public ScheduledTestingResponse scheduleTesting(UUID lbp, ScheduledTestingRequest request, String token) {
        LocalDateTime day = request.getDateAndTime().truncatedTo(ChronoUnit.DAYS);
        Lock dayLock = lockRegistry.obtain(day.toString());
        Lock patientLock = lockRegistry.obtain(String.valueOf(lbp));
        try {
            if (!dayLock.tryLock(1, TimeUnit.SECONDS) || !patientLock.tryLock(1, TimeUnit.SECONDS)) {
                String errMessage = "Neko već pokušava da ubaci pregled za prosledjeni dan ili za pacijenta";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
            log.info(String.format("Locking for patient %s and day %s", lbp, day));
        } catch (BadRequestException e) {
            throw e;
        } catch (InterruptedException e) {
            log.info(e.getMessage());
            throw new InternalServerErrorException("Greška pri zaključavanju baze");
        }

        ScheduledTestingResponse response;
        try {
            response = scheduleTestingLocked(lbp, request, token);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            dayLock.unlock();
            patientLock.unlock();
            log.info(String.format("Unlocking for patient %s and day %s", lbp, day));
        }
        return response;
    }

    private ScheduledTestingResponse scheduleTestingLocked(UUID lbp,
                                                           ScheduledTestingRequest request,
                                                           String token) {

        LocalDateTime requestDate = request.getDateAndTime().truncatedTo(ChronoUnit.MINUTES);
        request.setDateAndTime(requestDate);

        Patient patient = patientService.findPatient(lbp);
        ScheduledTesting scheduledTesting = testingMapper.scheduledTestingRequestToModel(patient, request);
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        AvailableTerm availableTerm;

        checkDateInFuture(request.getDateAndTime());
        checkPatientTestingsForDay(patient, request.getDateAndTime());

        LocalDateTime startDateAndTime = request.getDateAndTime().minusMinutes(ScheduledTesting.getTestDurationMinutes());
        LocalDateTime endDateAndTime = request.getDateAndTime().plusMinutes(ScheduledTesting.getTestDurationMinutes());
        List<AvailableTerm> availableTerms = availableTermRepository.findByDateAndTimeBetweenAndPbo(
                startDateAndTime,
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
        dateAndTime = dateAndTime.truncatedTo(ChronoUnit.MINUTES);
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
        Patient patient = lbp == null ? null : patientService.findPatient(lbp);
        LocalDateTime dateTime = date == null ? null : date.atStartOfDay();

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
        ScheduledTesting scheduledTesting = null;

        if (request.getScheduledTestingId() != null) {
            scheduledTesting = findScheduledTesting(request.getScheduledTestingId());
        }

        checkDateInPast(request.getCollectedInfoDate());

        patientCondition = patientConditionRepository.save(patientCondition);

        testing.setPatientCondition(patientCondition);
        testing = testingRepository.save(testing);

        if (scheduledTesting != null) {
            scheduledTesting.setTesting(testing);
            scheduledTestingRepository.save(scheduledTesting);
        }

        log.info(String.format("Kreirano testiranje u terminu %s za pacijenta sa lbp-om %s",
                testing.getDateAndTime().toString(),
                patient.getLbp()));
        return testingMapper.testingToResponse(testing, patientCondition);
    }

    @Override
    public ScheduledTestingResponse changeScheduledTestingStatus(Long scheduledTestingId,
                                                                 String testingStatusString,
                                                                 String patientArrivalStatusString) {

        if (testingStatusString == null && patientArrivalStatusString == null) {
            String errMessage = "Mora da se prosledi bar jedan novi status (status testiranja ili status o prispeću pacijenta)";
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        ScheduledTesting scheduledTesting = findScheduledTesting(scheduledTestingId);

        if (testingStatusString != null) {
            ExaminationStatus testStatus = findExaminationStatus(testingStatusString);
            scheduledTesting.setTestStatus(testStatus);

            switch (testStatus) {
                case U_TOKU:
                    scheduledTesting.setPatientArrivalStatus(PRIMLJEN);
                    break;
                case ZAVRSENO:
                    scheduledTesting.setPatientArrivalStatus(ZAVRSIO);
                    break;
            }
        }

        if (patientArrivalStatusString != null) {
            PatientArrivalStatus patientArrivalStatus = findPatientArrivalStatus(patientArrivalStatusString);
            scheduledTesting.setPatientArrivalStatus(patientArrivalStatus);

            if (patientArrivalStatus == OTKAZAO) {
                scheduledTesting.setTestStatus(OTKAZANO);
            }
        }

        scheduledTesting = scheduledTestingRepository.save(scheduledTesting);
        return testingMapper.scheduledTestingToResponse(scheduledTesting);
    }

    @Override
    public ScheduledTestingResponse deleteScheduledTesting(Long id) {
        ScheduledTesting scheduledTesting = findScheduledTesting(id);
        AvailableTerm availableTerm = scheduledTesting.getAvailableTerm();

        availableTerm.removeScheduledTesting(scheduledTesting);
        availableTerm.decrementScheduledTermsNum();

        scheduledTestingRepository.delete(scheduledTesting);
        availableTermRepository.save(availableTerm);

        log.info(String.format("Zakazano testiranje sa id-jem %s je obrisano", id));
        return testingMapper.scheduledTestingToResponse(scheduledTesting);
    }

    @Override
    public TestingListResponse processingOfTestResults(Pageable pageable) {
        Page<Testing> testingPage = testingRepository.findTestingByTestResult(TestResult.NEOBRADJEN, pageable);
        return testingMapper.testingPageToResponse(testingPage);
    }

    @Override
    public TestingResponse updateTestResult(Long id, String testResultString) {
        Testing testing = testingRepository.findById(id)
                .orElseThrow(() -> {
                    String errMessage = String.format("Testiranje sa id-jem '%d' nije pronadjeno", id);
                    log.error(errMessage);
                    throw new NotFoundException(errMessage);
                });

        TestResult testResult = TestResult.valueOfNotation(testResultString);
        if (testResult == null) {
            String errMessage = String.format("Rezultat testiranja '%s' ne postoji", testResultString);
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }

        testing.setTestResult(testResult);
        testing = testingRepository.save(testing);

        if (testResult.equals(TestResult.POZITIVAN) || testResult.equals(TestResult.NEGATIVAN)) {
            covidCertificateService.createCertificate(testing);
        }

        log.info("Rezultat testiranja za testiranje sa id-jem {} je promenjeno na '{}'", id, testResult);
        return testingMapper.testingToResponse(testing);
    }

    @Transactional
    @Override
    public List<LocalDateTime> removeNurseFromTerms(UpdateTermsNewShiftRequest request) {
        LocalDateTime day = request.getOldShift().getStartTime().truncatedTo(ChronoUnit.DAYS);
        Lock dayLock = lockRegistry.obtain(day.toString());
        try {
            if (!dayLock.tryLock(1, TimeUnit.SECONDS)) {
                String errMessage = "Neko već pokušava da ubaci pregled za prosledjeni dan";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
            log.info(String.format("Locking for and day %s", day));
        } catch (BadRequestException e) {
            throw e;
        } catch (InterruptedException e) {
            log.info(e.getMessage());
            throw new InternalServerErrorException("Greška pri zaključavanju baze");
        }

        List<LocalDateTime> response;
        try {
            response = removeNurseFromTermsLocked(request);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            dayLock.unlock();
            log.info(String.format("Unlocking for day %s", day));
        }
        return response;
    }

    private List<LocalDateTime> removeNurseFromTermsLocked(UpdateTermsNewShiftRequest request) {
        TimeRequest oldShift = request.getOldShift();
        TimeRequest newShift = request.getNewShift();

        List<AvailableTerm> oldShiftTerms = availableTermRepository.findByTimeSlot(
                oldShift.getStartTime(),
                oldShift.getEndTime()
        );

        List<AvailableTerm> newShiftTerms = availableTermRepository.findByTimeSlot(
                newShift.getStartTime(),
                newShift.getEndTime()
        );

        entityManager.clear();

        for (AvailableTerm term : oldShiftTerms) {
            term.decrementAvailableNursesNum();
        }

        for (AvailableTerm term : newShiftTerms) {
            term.incrementAvailableNursesNum();
        }

        List<AvailableTerm> notEnoughNurses = new ArrayList<>();
        Set<AvailableTerm> distinctTerms = new HashSet<>(oldShiftTerms);
        distinctTerms.addAll(newShiftTerms);

        for (AvailableTerm term : distinctTerms) {
            if (term.getAvailableNursesNum() == term.getScheduledTermsNum()) {
                term.setAvailability(Availability.POTPUNO_POPUNJEN_TERMIN);
            } else if (term.getAvailableNursesNum() > term.getScheduledTermsNum()) {
                term.setAvailability(Availability.MOGUCE_ZAKAZATI_U_OVOM_TERMINU);
            } else {
                notEnoughNurses.add(term);
            }
        }

        if (notEnoughNurses.isEmpty()) {
            availableTermRepository.saveAll(distinctTerms);
        }

        return notEnoughNurses.stream()
                .map(AvailableTerm::getDateAndTime)
                .collect(Collectors.toList());
    }

    private void checkDateInFuture(LocalDateTime date) {
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
        TimeRequest timeRequest = new TimeRequest(
                dateAndTime,
                dateAndTime.plusMinutes(ScheduledTesting.getTestDurationMinutes())
        );
        int availableNurses = getAvailableNurses(tokenPayload.getPbo(), timeRequest, token);
        return testingMapper.makeAvailableTerm(dateAndTime,
                tokenPayload.getPbo(),
                availableNurses);
    }

    private int getAvailableNurses(UUID pbo, TimeRequest request, String token) {
        int availableNurses;
        try {
            availableNurses = HttpUtils.getNumOfCovidNursesForDepartment(pbo, request, token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }

        if (availableNurses < 1) {
            String errMessage = String.format("Nema dostupnih sestara za departman sa pbo-om %s za termin '%s'", pbo, request.getStartTime());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
        return availableNurses;
    }

    private ScheduledTesting findScheduledTesting(Long id) {
        return scheduledTestingRepository.findById(id).orElseThrow(() -> {
            String errMessage = String.format("Zakazano testiranje sa id-jem %s ne postoji", id);
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        });
    }

    private ExaminationStatus findExaminationStatus(String examinationStatusString) {
        ExaminationStatus examinationStatus = ExaminationStatus.valueOfNotation(examinationStatusString);
        if (examinationStatus == null) {
            String errMessage = String.format("Status pregleda '%s' ne postoji", examinationStatusString);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
        return examinationStatus;
    }

    private PatientArrivalStatus findPatientArrivalStatus(String patientArrivalStatusString) {
        PatientArrivalStatus patientArrivalStatus = PatientArrivalStatus.valueOfNotation(patientArrivalStatusString);
        if (patientArrivalStatus == null) {
            String errMessage = String.format("Status o prispeću pacijenta '%s' ne postoji", patientArrivalStatusString);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
        return patientArrivalStatus;
    }
}
