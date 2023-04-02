package com.raf.si.patientservice.dto.request;

import com.raf.si.patientservice.model.Diagnosis;
import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@NoArgsConstructor
@Data
public class CreateExaminationReportRequest {

    private Boolean confidential;

    private String mainSymptoms;

    private String currentIllness;

    private String anamnesis;

    private String familyAnamnesis;

    private String patientOpinion;

    @NotEmpty(message = "polje 'objectiveFinding' ne sme biti prazno")
    private String objectiveFinding;

    private String suggestedTherapy;

    private String advice;

    private String diagnosis;

    private Boolean existingDiagnosis;

    private String treatmentResult;

    private String currentStateDescription;
}
