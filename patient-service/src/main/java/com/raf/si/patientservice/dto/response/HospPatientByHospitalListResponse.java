package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class HospPatientByHospitalListResponse {

    private final List<HospPatientByHospitalResponse> list;
    private final Long count;
}
