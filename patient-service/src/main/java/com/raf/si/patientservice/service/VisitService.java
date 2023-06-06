package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.VisitRequest;
import com.raf.si.patientservice.dto.response.VisitListResponse;
import com.raf.si.patientservice.dto.response.VisitResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VisitService {

    VisitResponse createVisit(UUID lbp, VisitRequest visitRequest);

    VisitListResponse getVisits(UUID lbp, Pageable pageable);
}
