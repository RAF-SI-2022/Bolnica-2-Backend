package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledTestingListResponse {
    private List<ScheduledTestingResponse> scheduledTestingList;
    private Long count;
}
