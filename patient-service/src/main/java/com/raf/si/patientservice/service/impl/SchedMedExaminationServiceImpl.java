package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamListResponse;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.*;
import com.raf.si.patientservice.mapper.SchedMedExamMapper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.repository.filtering.filter.ScheduledMedExamFilter;
import com.raf.si.patientservice.repository.filtering.specification.ScheduledMedExamSpecification;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import com.raf.si.patientservice.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;


@Slf4j
@Service
public class SchedMedExaminationServiceImpl implements SchedMedExaminationService {

    private final ScheduledMedExamRepository scheduledMedExamRepository;
    private final PatientService patientService;
    private final SchedMedExamMapper schedMedExamMapper;

    private final LockRegistry lockRegistry;
    @Value("${duration.of.exam}")
    private int DURATION_OF_EXAM;

    public SchedMedExaminationServiceImpl(ScheduledMedExamRepository scheduledMedExamRepository,
                                          PatientService patientService,
                                          SchedMedExamMapper schedMedExamMapper,
                                          JdbcLockRegistry lockRegistry) {
        this.scheduledMedExamRepository = scheduledMedExamRepository;
        this.patientService = patientService;
        this.schedMedExamMapper = schedMedExamMapper;
        this.lockRegistry = lockRegistry;
    }


    @Override
    @Transactional
    public SchedMedExamResponse createSchedMedExamination(SchedMedExamRequest schedMedExamRequest, String token) {

        /**
         * Check if there are any ongoing appointments for the requested doctor at the requested time,
         * taking into account that each examination is assumed to have a duration of DURATION_OF_EXAM minutes.
         *
         * @param DURATION_OF_EXAM
         */

        Lock doctorLock = lockRegistry.obtain(String.valueOf(schedMedExamRequest.getLbzDoctor()));
        Lock patientLock = lockRegistry.obtain(String.valueOf(schedMedExamRequest.getLbp()));
        try{
            if(!doctorLock.tryLock(1, TimeUnit.SECONDS) || !patientLock.tryLock(1, TimeUnit.SECONDS)){
                String errMessage = "Neko već pokušava da ubaci pregled za doktora ili za pacijenta";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
            log.info(String.format("Locking for patient %s and doctor %s",
                    schedMedExamRequest.getLbp(),
                    schedMedExamRequest.getLbzDoctor()));

        } catch (BadRequestException e) {
            throw e;
        }catch(InterruptedException e){
            log.info(e.getMessage());
            throw new InternalServerErrorException("Greška pri zaključavanju baze");
        }

        SchedMedExamResponse response;
        try{
            response = createSchedMedExaminationLocked(schedMedExamRequest, token);
        }catch(RuntimeException e) {
            throw e;
        }finally{
            doctorLock.unlock();
            patientLock.unlock();
            log.info(String.format("Unlocking for patient %s and doctor %s",
                    schedMedExamRequest.getLbp(),
                    schedMedExamRequest.getLbzDoctor()));

        }
        return response;
    }

    @Override
    public SchedMedExamResponse createSchedMedExaminationLocked(SchedMedExamRequest schedMedExamRequest, String token){
        Date appointmnet = schedMedExamRequest.getAppointmentDate();
        Date timeBeforeAppointments = new Date(appointmnet.getTime() - DURATION_OF_EXAM * 60 * 1000);
        Date timeAfterAppointments = new Date(appointmnet.getTime() + DURATION_OF_EXAM * 60 * 1000);

        checkAppointmentDate(appointmnet);

        List<ScheduledMedExamination> exams = scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBeforeAppointments,
                timeAfterAppointments, schedMedExamRequest.getLbzDoctor()).orElse(Collections.emptyList());

        boolean hasUncompletedExams = exams.stream()
                .anyMatch(exam -> exam.getExaminationStatus() != ExaminationStatus.ZAVRSENO);

        if (hasUncompletedExams) {
            String errMessage = String.format("Obustavljeno zakazivanje, dolazi do preklapanja pregleda. Potrebno je imati %d minuta " +
                            "između svakog zakazanog pregleda. Preklapa se sa pregledom id: %d", DURATION_OF_EXAM,
                    exams.get(0).getId());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        /**
         * Checking if there is a referred doctor
         */
        isGivenLbzDoctors(schedMedExamRequest.getLbzDoctor(),token);


        Patient patient=patientService.findPatient(schedMedExamRequest.getLbp());

        checkPatientExams(patient, appointmnet, schedMedExamRequest.getLbzDoctor());

        ScheduledMedExamination scheduledMedExamination = schedMedExamMapper.schedMedExamRequestToScheduledMedExamination
                (new ScheduledMedExamination(), schedMedExamRequest, patient);

        if (schedMedExamRequest.getCovid() != null) {
            scheduledMedExamination.setCovid(schedMedExamRequest.getCovid());
        }

        scheduledMedExamRepository.save(scheduledMedExamination);

        log.info("Pregled ušpesno kreiran");
        return schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination);
    }

    @Override
    public SchedMedExamResponse updateSchedMedExaminationExamStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest) {
        ScheduledMedExamination scheduledMedExamination= findSchedMedExamById(updateSchedMedExamRequest.getId());

        scheduledMedExamination= schedMedExamMapper.updateSchedMedExamRequestToScheduledMedExaminationExamStatus
                (scheduledMedExamination, updateSchedMedExamRequest);

        scheduledMedExamRepository.save(scheduledMedExamination);

        log.info(String.format("Izmena statusa pregleda sa id '%d' uspešno sacuvana", updateSchedMedExamRequest.getId()));
        return schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination);
    }

    @Transactional
    @Override
    public SchedMedExamResponse deleteSchedMedExamination(Long id) {
        ScheduledMedExamination scheduledMedExamination= findSchedMedExamById(id);

        scheduledMedExamRepository.delete(scheduledMedExamination);

        log.info(String.format("Zakazani pregled sa id '%d' uspešno izbrisan", id));
        return schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination);
    }


    @Transactional
    @Override
    public SchedMedExamListResponse getSchedMedExaminationByLbz(UUID lbz, Date appointmentDate
            , String token, Pageable pageable) {

        isGivenLbzDoctors(lbz,token);

        ScheduledMedExamFilter scheduledMedExamFilter= new ScheduledMedExamFilter(lbz, appointmentDate);
        ScheduledMedExamSpecification specification= new ScheduledMedExamSpecification(scheduledMedExamFilter);

        Page<ScheduledMedExamination> medExaminationPage= scheduledMedExamRepository.findAll(specification, pageable);

        log.info(String.format("Uspešno pronadjeni zakazani pregledi za doktora sa lbz-om '%s'", lbz));
        return schedMedExamMapper.schedMedExamPageToSchedMedExamListResponse(medExaminationPage);
    }

    @Override
    public SchedMedExamResponse updateSchedMedExaminationPatientArrivalStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest) {
        ScheduledMedExamination scheduledMedExamination= findSchedMedExamById(updateSchedMedExamRequest.getId());

        scheduledMedExamination= schedMedExamMapper.updateSchedMedExamRequestToScheduledMedExaminationPatientArrivalStatus(
                scheduledMedExamination, updateSchedMedExamRequest);

        scheduledMedExamRepository.save(scheduledMedExamination);

        log.info(String.format("Izmena statusa o prispeću pacijenta sa id '%d' uspešno sacuvana", updateSchedMedExamRequest.getId()));
        return schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination);

    }

    @Override
    public ScheduledMedExamination findSchedMedExamById(Long id) {
        return scheduledMedExamRepository.findById(id)
                .orElseThrow(()->{
                    String errMessage = String.format("Zakazani pregled sa id-om '%s' ne postoji", id);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });
    }

    private int max(int first, int... rest) {
        int ret = first;
        for (int val : rest) {
            ret = Math.max(ret, val);
        }
        return ret;
    }

    private void isGivenLbzDoctors(UUID lbz, String token) throws RuntimeException{
        ResponseEntity<UserResponse> response;

        /**
         * checking whether the employee is a doctor, as well as whether there is an
         * employee with a forwarded lbz.
         */
        try {

            response = HttpUtils.findUserByLbz(token, lbz);
            UserResponse responseBody = response.getBody();

            int isDoctor = max(responseBody.getPermissions().indexOf("ROLE_DR_SPEC_ODELJENJA")
                    , responseBody.getPermissions().indexOf("ROLE_DR_SPEC")
                    , responseBody.getPermissions().indexOf("ROLE_DR_SPEC_POV"));

             if (isDoctor == -1) {
                String errMessage = String.format("Zaposleni sa id-om '%s' nije doktor", lbz);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
             }


        } catch (IllegalArgumentException e) {
            String errMessage = String.format("Error when calling user service: " + e.getMessage());
            log.info(errMessage);
            throw new InternalServerErrorException("Error when calling user service: " + e.getMessage());
        }catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                String errMessage = String.format("Zaposleni sa id-om '%s' ne postoji", lbz);
                log.info(errMessage);
                throw new NotFoundException(errMessage);
            }
            throw new InternalServerErrorException("Error when calling user service: " + e.getMessage());
        }
    }

    private void checkAppointmentDate(Date appointmentDate){
        if(appointmentDate.before(new Date())){
            String errMessage = "Pregled ne može da se zakaže za datum koji je prosao";
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void checkPatientExams(Patient patient, Date appointmentDate, UUID lbz) throws RuntimeException{
        Date startDate = DateUtils.truncate(appointmentDate, Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.addDays(startDate, 1);

        List<ScheduledMedExamination> patientExams = scheduledMedExamRepository
                .findByPatientAndAppointmentDateBetween(
                    patient,
                    startDate,
                    endDate
                ).orElse(Collections.emptyList());

        Date appointmentDurationDate = new Date(appointmentDate.getTime() + DURATION_OF_EXAM * 60 * 1000);
        for(ScheduledMedExamination exam: patientExams){
            if(exam.getLbzDoctor().equals(lbz)){
                String errMessage = "Pacijent ne može imati više pregleda kod istog doktora u jednom danu";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }

            Date examDate = exam.getAppointmentDate();
            Date examDurationDate = new Date(examDate.getTime() + DURATION_OF_EXAM * 60 * 1000);
            if(!(examDurationDate.before(appointmentDate) || appointmentDurationDate.before(examDate))){
                String errMessage = "Pacijent već ima zakazan pregled u prosledjenom terminu";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }
    }
}

