package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamListResponse;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.PatientMapper;
import com.raf.si.patientservice.mapper.SchedMedExamMapper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.repository.filtering.filter.ScheduledMedExamFilter;
import com.raf.si.patientservice.repository.filtering.specification.ScheduledMedExamSpecification;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import com.raf.si.patientservice.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SchedMedExaminationServiceImpl implements SchedMedExaminationService {

    private final ScheduledMedExamRepository scheduledMedExamRepository;
    private final PatientRepository patientRepository;
    private final SchedMedExamMapper schedMedExamMapper;
    private  final PatientMapper patientMapper;
    @Value("${duration.of.exam}")
    private int DURATION_OF_EXAM;

    public SchedMedExaminationServiceImpl(ScheduledMedExamRepository scheduledMedExamRepository, PatientRepository patientRepository, SchedMedExamMapper schedMedExamMapper, PatientMapper patientMapper) {
        this.scheduledMedExamRepository = scheduledMedExamRepository;
        this.patientRepository = patientRepository;
        this.schedMedExamMapper = schedMedExamMapper;
        this.patientMapper = patientMapper;
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

        Date appointmnet = schedMedExamRequest.getAppointmentDate();
        Date timeBetweenAppointmnets = new Date(appointmnet.getTime() - DURATION_OF_EXAM * 60 * 1000);

        List<ScheduledMedExamination> exams = scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(timeBetweenAppointmnets,
                appointmnet, schedMedExamRequest.getLbzDoctor()).orElse(Collections.emptyList());

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

        /**
         * Checking if there is a referred patient in the database, there should be.
         */
        Patient patient=patientRepository.findByLbpAndDeleted(schedMedExamRequest.getLbp(), false).orElseThrow(() -> {
            String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", schedMedExamRequest.getLbp());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        });

        ScheduledMedExamination scheduledMedExamination = schedMedExamMapper.schedMedExamRequestToScheduledMedExamination
                (new ScheduledMedExamination(), schedMedExamRequest, patient);

        scheduledMedExamRepository.save(scheduledMedExamination);

        log.info("Pregled ušpesno kreiran");
        return schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination);
    }

    @Override
    public SchedMedExamResponse updateSchedMedExaminationExamStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest) {
        /**
         * Checking if there is an appointment in database with the passed id
         */
        ScheduledMedExamination scheduledMedExamination = scheduledMedExamRepository.findById(updateSchedMedExamRequest.getId())
                .orElseThrow(() -> {
                    String errMessage = String.format("Zakazani pregled sa id-om '%s' ne postoji", updateSchedMedExamRequest.getId());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        scheduledMedExamination = schedMedExamMapper.updateSchedMedExamRequestToScheduledMedExaminationExamStatus(scheduledMedExamination,
                updateSchedMedExamRequest);

        scheduledMedExamRepository.save(scheduledMedExamination);

        log.info(String.format("Izmena statusa pregleda sa id '%d' uspešno sacuvana", updateSchedMedExamRequest.getId()));
        return schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination);
    }

    @Transactional
    @Override
    public SchedMedExamResponse deleteSchedMedExamination(Long id) {
        /**
         * Checking if there is an appointment in database with the passed id
         */
        ScheduledMedExamination scheduledMedExamination=scheduledMedExamRepository.findById(id)
                .orElseThrow(()->{
                    String errMessage = String.format("Zakazani pregled sa id-om '%s' ne postoji", id);

                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });
        Patient patient= scheduledMedExamination.getPatient();
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

        log.info(String.format("Uspesno pronadjeni zakazani pregledi za docu lbza '%s'", lbz));
        return schedMedExamMapper.schedMedExamPageToSchedMedExamListResponse(medExaminationPage);
    }

    @Override
    public SchedMedExamResponse updateSchedMedExaminationPatientArrivalStatus(UpdateSchedMedExamRequest updateSchedMedExamRequest) {
        /**
         * Checking if there is an appointment in database with the passed id
         */
        ScheduledMedExamination scheduledMedExamination=scheduledMedExamRepository.findById(updateSchedMedExamRequest.getId())
                .orElseThrow(()->{
                    String errMessage = String.format("Zakazani pregled sa id-om '%s' ne postoji", updateSchedMedExamRequest.getId());
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                });

        scheduledMedExamination= schedMedExamMapper.updateSchedMedExamRequestToScheduledMedExaminationPatientArrivalStatus(
                scheduledMedExamination, updateSchedMedExamRequest);

        scheduledMedExamRepository.save(scheduledMedExamination);

        log.info(String.format("Izmena statusa o prispeću pacijenta sa id '%d' uspešno sacuvana", updateSchedMedExamRequest.getId()));
        return schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(scheduledMedExamination);

    }
    
    private int max(int first, int... rest) {
        int ret = first;
        for (int val : rest) {
            ret = Math.max(ret, val);
        }
        return ret;
    }

    /*
    private SchedMedExamListResponse schedMedExamPageToSchedMedExamExtendListResponse(Page<ScheduledMedExamination> medExaminationPage) {
        SchedMedExamListResponse schedMedExamListResponse=schedMedExamMapper.schedMedExamPageToSchedMedExamListResponse
                (medExaminationPage);

        List<SchedMedExamExtendedResponse> updatedResponses = schedMedExamListResponse.getSchedMedExamResponseList()
                .stream()
                .map(schedMedExamResponse -> {
                    UUID lbp = schedMedExamResponse.getLbp();
                    PatientResponse patientResponse = patientMapper.patientToPatientResponse(patientRepository.findByLbp(lbp).get());
                    schedMedExamResponse.setPatientResponse(patientResponse);
                    return schedMedExamResponse;
                }).collect(Collectors.toList());

        schedMedExamListResponse.setSchedMedExamResponseList(updatedResponses);
        return schedMedExamListResponse;
    }
*/
    private void isGivenLbzDoctors(UUID lbz, String token){
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
}

