package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.MedicalExamination;
import com.raf.si.patientservice.model.MedicalHistory;
import com.raf.si.patientservice.model.Operation;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class HealthRecordResponse {
    private Long id;
    private Date registrationDate;
    private BloodType bloodType;
    private RHFactor rhFactor;
    private AllergyListResponse allergies;
    private VaccinationListResponse vaccinations;
    private OperationListResponse operations;
    private MedicalHistoryListResponse medicalHistory;
    private MedicalExaminationListResponse medicalExaminations;
    private UUID patientLbp;
}
