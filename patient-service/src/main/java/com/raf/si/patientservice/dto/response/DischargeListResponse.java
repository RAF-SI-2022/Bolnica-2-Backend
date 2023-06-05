package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class DischargeListResponse {

    private final List<DischargeResponse> discharges;
    private final Long count;
}
