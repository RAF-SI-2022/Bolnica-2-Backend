package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LightHealthRecordResponse {
    private Long id;
    private BloodType bloodType;
    private RHFactor rhFactor;
    private AllergyListResponse allergies;
    private VaccinationListResponse vaccinations;
    private UUID patientLbp;
}
