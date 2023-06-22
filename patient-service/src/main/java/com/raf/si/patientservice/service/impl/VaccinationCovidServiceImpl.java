package com.raf.si.patientservice.service.impl;


import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.request.VaccinationCovidRequest;
import com.raf.si.patientservice.dto.response.DosageReceivedResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationListResponse;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.dto.response.VaccinationCovidResposne;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.VaccinationMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.ScheduledVaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccineRepository;
import com.raf.si.patientservice.repository.filtering.filter.ScheduledVaccinationCovidFilter;
import com.raf.si.patientservice.repository.filtering.specification.ScheduledVaccinationCovidSpecification;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.VaccinationCovidService;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class VaccinationCovidServiceImpl implements VaccinationCovidService {

    private final VaccinationCovidRepository vaccinationCovidRepository;
    private final ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository;
    private final AvailableTermRepository availableTermRepository;
    private final VaccineRepository vaccineRepository;
    private final PatientService patientService;
    private final VaccinationMapper vaccinationMapper;
    private final LockRegistry lockRegistry;

    public VaccinationCovidServiceImpl(VaccinationCovidRepository vaccinationCovidRepository
            , ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository
            , AvailableTermRepository availableTermRepository
            , PatientService patientService
            , VaccinationMapper vaccinationMapper
            , LockRegistry lockRegistry
            , VaccineRepository vaccineRepository) {
        this.vaccinationCovidRepository = vaccinationCovidRepository;
        this.scheduledVaccinationCovidRepository = scheduledVaccinationCovidRepository;
        this.availableTermRepository = availableTermRepository;
        this.patientService = patientService;
        this.vaccinationMapper = vaccinationMapper;
        this.lockRegistry = lockRegistry;
        this.vaccineRepository = vaccineRepository;
    }

    @Override
    public ScheduledVaccinationResponse scheduleVaccination(UUID lbp, ScheduledVaccinationRequest request, String token) {

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

        ScheduledVaccinationResponse response;
        try{
            response = scheduleVaccinationLocked(lbp, request, token);
        }catch(RuntimeException e) {
            throw e;
        }finally{
            dayLock.unlock();
            patientLock.unlock();
            log.info(String.format("Unlocking for patient %s and day %s", lbp, day));
        }
        return response;
    }

    private ScheduledVaccinationResponse scheduleVaccinationLocked(UUID lbp, ScheduledVaccinationRequest request, String token) {
        LocalDateTime requestDate = request.getDateAndTime().truncatedTo(ChronoUnit.SECONDS);
        request.setDateAndTime(requestDate);

        Patient patient = patientService.findPatient(lbp);
        ScheduledVaccinationCovid scheduledVaccinationCovid = vaccinationMapper.scheduledVaccinationRequestToModel(patient, request);
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

        scheduledVaccinationCovid.setAvailableTerm(availableTerm);
        scheduledVaccinationCovid = scheduledVaccinationCovidRepository.save(scheduledVaccinationCovid);

        log.info(String.format("Kreirano novo zakazana vakcinacija za pacijenta sa lbp-om %s, broj slobodnih termina za termin %s je %d",
                patient.getLbp(),
                availableTerm.getDateAndTime().toString(),
                (availableTerm.getAvailableNursesNum() - availableTerm.getScheduledTermsNum())));


        return vaccinationMapper.scheduledVaccinationToResponse(scheduledVaccinationCovid);
    }

    @Override
    public ScheduledVaccinationListResponse getScheduledVaccinations(UUID lbp, LocalDate date, Pageable pageable) {
        Patient patient = lbp == null? null: patientService.findPatient(lbp);
        LocalDateTime dateTime = date == null? null: date.atStartOfDay();

        ScheduledVaccinationCovidFilter filter = new ScheduledVaccinationCovidFilter(patient, dateTime);
        ScheduledVaccinationCovidSpecification spec = new ScheduledVaccinationCovidSpecification(filter);

        Page<ScheduledVaccinationCovid> scheduledTestingPage = scheduledVaccinationCovidRepository.findAll(spec, pageable);
        return vaccinationMapper.scheduledVaccinationPageToResponse(scheduledTestingPage);
    }

    @Override
    @Transactional
    public VaccinationCovidResposne createVaccination(UUID lbp, VaccinationCovidRequest request, String token) {
        Patient patient = patientService.findPatient(lbp);
        VaccinationCovid vaccinationCovid = vaccinationMapper.vaccCovidRequestToModel(request);
        Optional<Vaccine> vaccine = vaccineRepository.findByName(request.getVaccineName());

        if (!vaccine.isPresent()){
            String err = String.format("Vaccine with the name '%s', doesnt exits ", request.getVaccineName());
            log.error(err);
            throw  new BadRequestException(err);
        }
        vaccinationCovid.setVaccine(vaccine.get());

        if (!patient.getHealthRecord().getId().equals(request.getHealthRecordId())){
            String err = String.format("Given Health record '%s' doesnt match  with patient health record '%s' "
                    , request.getHealthRecordId(), patient.getHealthRecord().getId());
            log.error(err);
            throw  new BadRequestException(err);
        }
        vaccinationCovid.setHealthRecord(patient.getHealthRecord());

        checkDateInPast(Date.from(request.getDateTime().atZone(ZoneId.systemDefault()).toInstant()));

        Optional<ScheduledVaccinationCovid> scheduledVaccinationCovid = scheduledVaccinationCovidRepository.findById(request.getVaccinationId());
        if (!scheduledVaccinationCovid.isPresent()){
            String err = String.format("Given scheduled vaccination id '%s' does not exit."
                    , request.getVaccinationId());
            log.error(err);
            throw  new BadRequestException(err);
        }

        if (scheduledVaccinationCovid.get().getVaccination() != null){
            String err = String.format("Given scheduled vaccination id '%s' already has vaccination created."
                    , request.getVaccinationId());
            log.error(err);
            throw  new BadRequestException(err);
        }

        vaccinationCovid.setScheduledVaccinationCovid(scheduledVaccinationCovid.get());
        vaccinationCovid.setPerformerLbz(TokenPayloadUtil.getTokenPayload().getLbz());
        vaccinationCovid= vaccinationCovidRepository.save(vaccinationCovid);

        ScheduledVaccinationCovid svc = scheduledVaccinationCovid.get();
        svc.setVaccination(vaccinationCovid);
        scheduledVaccinationCovidRepository.save(svc);

        log.info(String.format("Kreirana vakcinacija u terminu %s za pacijenta sa lbp-om %s",
                request.getDateTime().toString(),
                patient.getLbp()));
        return vaccinationMapper.vaccinationCovidToResponse(vaccinationCovid);
    }

    @Override
    public DosageReceivedResponse getPatientDosageReceived(UUID lbp) {
        Patient patient= patientService.findPatient(lbp);

        List<VaccinationCovid> vaccList = vaccinationCovidRepository.findByHealthRecord_Patient(patient);
        Optional<VaccinationCovid> vaccinationCovid = vaccList.stream()
                .max(Comparator.comparingLong(VaccinationCovid::getDosageAsLong));

        if (vaccinationCovid.isPresent()) {
            log.info(String.format("Patient with lbp '%s' is not present in vaccinationCovid repository return 0 value", lbp));
            return vaccinationMapper.vaccinationCovidToDosageReceived(vaccinationCovid.get().getDoseReceived());
        }
        return vaccinationMapper.vaccinationCovidToDosageReceived("0");
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
        return vaccinationMapper.makeAvailableTerm(dateAndTime,
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


    private void checkDateInPast(Date date) {
        Date currDate = new Date();
        if (currDate.before(date)) {
            String errMessage = String.format("Datum %s je u buducnosti", date);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }
    private void checkDateInFuture(LocalDateTime date){
        LocalDateTime currDate = LocalDateTime.now();
        if (currDate.isAfter(date)) {
            String errMessage = String.format("Datum %s je u proslosti", date);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void checkPatientTestingsForDay(Patient patient, LocalDateTime dateAndTime) {
        LocalDateTime startDate = dateAndTime.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endDate = startDate.plusDays(1);

        List<ScheduledVaccinationCovid> tests = scheduledVaccinationCovidRepository.findByPatientAndDateAndTimeBetween(patient, startDate, endDate);
        if (tests != null && tests.size() > 0) {
            String errMessage = String.format("Pacijent sa lbp-om %s vec ima zakazano testiranje za prosledjeni dan", patient.getLbp());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }
}
