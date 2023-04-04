package com.raf.si.patientservice.dto.request;

import com.raf.si.patientservice.model.enums.examination.ExaminationStatus;
import com.raf.si.patientservice.model.enums.examination.PatientArrivalStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateSchedMedExamRequest {

    @NotNull(message= "Potrebno je proslediti id zakazanog pregleda")
    private Long id;
    /**
     * @param newStatus variable may represent a string notation of {@link PatientArrivalStatus}
     *                  or {@link ExaminationStatus} class.
     */
    @NotEmpty(message = "Novi status ne sme biti prazan")
    private String newStatus;


}
