package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.SchedMedExamRequest;
import com.raf.si.patientservice.dto.response.SchedMedExamResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.SchedMedExamMapper;
import com.raf.si.patientservice.model.ScheduledMedExamination;
import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.repository.ScheduledMedExamRepository;
import com.raf.si.patientservice.service.SchedMedExaminationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class SchedMedExaminationImpl implements SchedMedExaminationService {

    private final ScheduledMedExamRepository scheduledMedExamRepository;
    private final SchedMedExamMapper schedMedExamMapper;

    /**
     *  Duration of scheduled medical examination expressed in minutes
     */
    private final int DURRATION_OF_EXAM= 45;

    public SchedMedExaminationImpl(ScheduledMedExamRepository scheduledMedExamRepository, SchedMedExamMapper schedMedExamMapper) {
        this.scheduledMedExamRepository = scheduledMedExamRepository;
        this.schedMedExamMapper = schedMedExamMapper;
    }


    @Override
    @Transactional
    public SchedMedExamResponse createSchedMedExamination(SchedMedExamRequest schedMedExamRequest) {

        Date appointmnet= schedMedExamRequest.getAppointmentDate();
        Date timeBetweenAppointmnets= new Date(appointmnet.getTime() -DURRATION_OF_EXAM * 60 * 1000);

        /**
         * Provera da li postoje vec zakazani pregledi za trazenog doktora. Smatracemo da svaki pregled traje 45 minuta.
         * Ukoliko dodje do menjanja trajanja pregleda promeni DURRATION_OF_EXAM
         */
        List<ScheduledMedExamination> exams= scheduledMedExamRepository.findByAppointmentDateBetweenAndLbz_doctor(timeBetweenAppointmnets,
                appointmnet, schedMedExamRequest.getLbz_doctor()).orElse(Collections.emptyList());

        boolean hasUncompletedExams = exams.stream()
                .anyMatch(exam -> exam.getExaminationStatus() != ExaminationStatus.ZAVRSENO);

        if (hasUncompletedExams) {
            String errMessage = String.format("Obustavljena zakazivanje, dolazi do preklapanja pregleda. Potrebno je imati ",
                    DURRATION_OF_EXAM ," minuta izmedju svakog zakazanog pregleda. Preklapa se sa id pregleda ",
                    exams.get(0).getId());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }




        return null;
    }
}
