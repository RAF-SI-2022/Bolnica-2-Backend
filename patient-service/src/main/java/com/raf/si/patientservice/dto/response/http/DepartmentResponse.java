package com.raf.si.patientservice.dto.response.http;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DepartmentResponse {

    private UUID pbo;
    private String name;
    private HospitalResponse hospitalResponse;
}
