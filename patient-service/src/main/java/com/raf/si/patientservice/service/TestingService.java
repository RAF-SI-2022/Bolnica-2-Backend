package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.response.ScheduledTestingResponse;

import java.util.UUID;

public interface TestingService {
    public ScheduledTestingResponse scheduleTesting(UUID lbp, ScheduledTestingRequest request, String token);
}
