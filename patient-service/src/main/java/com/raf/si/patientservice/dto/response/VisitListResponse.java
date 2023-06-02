package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class VisitListResponse {

    private final List<VisitResponse> visitResponseList;
    private final Long count;
}
