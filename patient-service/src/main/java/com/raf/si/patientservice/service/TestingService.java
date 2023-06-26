package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.ScheduledTestingRequest;
import com.raf.si.patientservice.dto.request.TestingRequest;
import com.raf.si.patientservice.dto.response.*;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface TestingService {
    ScheduledTestingResponse scheduleTesting(UUID lbp, ScheduledTestingRequest request, String token);

    AvailableTermResponse getAvailableTerm(LocalDateTime dateAndTime, String token);

    ScheduledTestingListResponse getScheduledtestings(UUID lbp, LocalDate date, Pageable pageable);

    TestingResponse createTesting(UUID lbp, TestingRequest request);

    ScheduledTestingResponse changeScheduledTestingStatus(Long scheduledTestingId, String testingStatusString, String patientArrivalStatusString);

    ScheduledTestingResponse deleteScheduledTesting(Long id);
    TestingListResponse processingOfTestResults(Pageable pageable);
    TestingResponse updateTestResult(Long id, String testResultString);
}
