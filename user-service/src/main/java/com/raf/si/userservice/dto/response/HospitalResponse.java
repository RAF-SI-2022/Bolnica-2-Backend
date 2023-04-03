package com.raf.si.userservice.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class HospitalResponse {

    private UUID pbb;
    private String fullName;
    private String shortName;
    private String place;
    private String address;
    private Date dateOfEstablishment;
    private String activity;
}
