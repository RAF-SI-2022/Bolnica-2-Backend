package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HospitalisedPatientsListResponse {

    private List<HospitalisedPatientsResponse> hospitalisedPatients;
    private Long count;
}
