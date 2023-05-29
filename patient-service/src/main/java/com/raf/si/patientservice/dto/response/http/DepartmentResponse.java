package com.raf.si.patientservice.dto.response.http;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class DepartmentResponse {

    private UUID pbo;
    private String name;
    private HospitalResponse hospitalResponse;
}
