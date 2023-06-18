package com.raf.si.patientservice.service.impl;


import com.raf.si.patientservice.dto.request.ScheduledVaccinationRequest;
import com.raf.si.patientservice.dto.response.ScheduledVaccinationResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.VaccinationMapper;
import com.raf.si.patientservice.model.AvailableTerm;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledTesting;
import com.raf.si.patientservice.model.ScheduledVaccinationCovid;
import com.raf.si.patientservice.model.enums.testing.Availability;
import com.raf.si.patientservice.repository.AvailableTermRepository;
import com.raf.si.patientservice.repository.ScheduledVaccinationCovidRepository;
import com.raf.si.patientservice.repository.VaccinationCovidRepository;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.VaccinationCovidService;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class VaccinationCovidServiceImpl implements VaccinationCovidService {

    private final VaccinationCovidRepository vaccinationCovidRepository;
    private final ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository;
    private final AvailableTermRepository availableTermRepository;
    private final PatientService patientService;
    private final VaccinationMapper vaccinationMapper;
    private final LockRegistry lockRegistry;

    public VaccinationCovidServiceImpl(VaccinationCovidRepository vaccinationCovidRepository
            , ScheduledVaccinationCovidRepository scheduledVaccinationCovidRepository
            , AvailableTermRepository availableTermRepository
            , PatientService patientService
            , VaccinationMapper vaccinationMapper
            , LockRegistry lockRegistry) {
        this.vaccinationCovidRepository = vaccinationCovidRepository;
        this.scheduledVaccinationCovidRepository = scheduledVaccinationCovidRepository;
        this.availableTermRepository = availableTermRepository;
        this.patientService = patientService;
        this.vaccinationMapper = vaccinationMapper;
        this.lockRegistry = lockRegistry;
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
