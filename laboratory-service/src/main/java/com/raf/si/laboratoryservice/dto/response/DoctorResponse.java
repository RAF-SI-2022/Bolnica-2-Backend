package com.raf.si.laboratoryservice.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class DoctorResponse {
    private UUID lbz;
    private String firstName;
    private String lastName;
}

