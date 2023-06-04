package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.response.AvailableTermResponse;
import com.raf.si.patientservice.dto.response.ScheduledTestingResponse;

import java.util.Date;
import java.util.UUID;

public interface TestingService {
    ScheduledTestingResponse scheduleTesting(UUID lbp, ScheduledTestingRequest request, String token);

    AvailableTermResponse getAvailableTerm(Date dateAndTime, String token);
}
