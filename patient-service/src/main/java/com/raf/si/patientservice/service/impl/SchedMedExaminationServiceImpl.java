package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.request.UpdateSchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.SchedMedExamMapper;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import com.raf.si.patientservice.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.*;


@Slf4j
@Service
public class SchedMedExaminationServiceImpl implements SchedMedExaminationService {

    private final ScheduledMedExamRepository scheduledMedExamRepository;
    private final PatientRepository patientRepository;
    private final SchedMedExamMapper schedMedExamMapper;
    @Value("${duration.of.exam}")
    private int DURATION_OF_EXAM;

    public SchedMedExaminationServiceImpl(ScheduledMedExamRepository scheduledMedExamRepository, PatientRepository patientRepository, SchedMedExamMapper schedMedExamMapper) {
        this.scheduledMedExamRepository = scheduledMedExamRepository;
        this.patientRepository = patientRepository;
        this.schedMedExamMapper = schedMedExamMapper;
    }


    @Override
    @Transactional
    public SchedMedExamResponse createSchedMedExamination(SchedMedExamRequest schedMedExamRequest) {

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
         * #TODO
         * Checking if there is a referred doctor
         */


        /**
         * Checking if there is a referred patient in the database, there should be.
         */
        patientRepository.findByLbp(schedMedExamRequest.getLbp()).orElseThrow(() -> {
            String errMessage = String.format("Pacijent sa lbp-om '%s' ne postoji", schedMedExamRequest.getLbp());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        });

        ScheduledMedExamination scheduledMedExamination = schedMedExamMapper.schedMedExamRequestToScheduledMedExamination
                (new ScheduledMedExamination(), schedMedExamRequest);

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

    @Override
    public Page<SchedMedExamResponse> getSchedMedExaminationByLbz(UUID lbz, Optional<Date> appointmentDate
            , String token, int pageNumber, int pageSize) {

        ResponseEntity<UserResponse> response = HttpUtils.findUserByLbz(token, lbz);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        /**
         * checking whether the employee is a doctor, as well as whether there is an
         * employee with a forwarded lbz.
         */
        Page<ScheduledMedExamination> medExaminationList;
        try {
            if (response.getStatusCode() == HttpStatus.OK) {
                UserResponse responseBody = response.getBody();


                int isDoctor = max(responseBody.getPermissions().indexOf("ROLE_DR_SPEC_ODELJENJA")
                        , responseBody.getPermissions().indexOf("ROLE_DR_SPEC")
                        , responseBody.getPermissions().indexOf("ROLE_DR_SPEC_POV"));

                if (isDoctor == -1) {
                    String errMessage = String.format("Zaposleni sa id-om '%s' nije doktor", lbz);
                    log.info(errMessage);
                    throw new BadRequestException(errMessage);
                }
                /**
                 * Checking if appointment date is passed if so then service should return all med exams for appointmentDate
                 * date for doctor with given lbz
                 */

                if(appointmentDate.isPresent()) {
                    /**
                     * Very Hacky way to get schedMedExams for passed day.
                     */
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(appointmentDate.get());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    Date endDate = calendar.getTime();

                    medExaminationList = scheduledMedExamRepository.findByAppointmentDateBetweenAndLbzDoctor(appointmentDate.get(),endDate
                                    , responseBody.getLbz(),pageable)
                            .orElse(Page.empty(pageable));
                }else
                    medExaminationList=scheduledMedExamRepository.findByLbzDoctor(responseBody.getLbz(), pageable).orElse( Page.empty(pageable));

            } else {
                String errMessage = String.format("Zaposleni sa id-om '%s' ne postoji", lbz);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        } catch (RestClientException e) {
            throw new InternalServerErrorException("Error calling other service: " + e.getMessage());
        }

        Page<SchedMedExamResponse> medExaminationResponseList=Page.empty(pageable);
        if(medExaminationList.hasContent()){
            medExaminationResponseList=medExaminationList.map(medExam -> schedMedExamMapper.scheduledMedExaminationToSchedMedExamResponse(medExam));
        }

        return medExaminationResponseList;
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
}
