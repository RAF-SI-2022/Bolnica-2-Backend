package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.Vaccine;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class VaccinationResponse {
    private Long id;
    private Vaccine vaccine;
    private Long healthRecordId;
    private Date vaccinationDate;
}
