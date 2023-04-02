package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class BasicHealthRecordResponse {

    private Long id;
    private BloodType bloodType;
    private RHFactor rhFactor;
    private UUID patientLbp;

}
