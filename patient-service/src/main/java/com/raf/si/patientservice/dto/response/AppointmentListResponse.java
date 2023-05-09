package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AppointmentListResponse {
    private List<AppointmentResponse> appointments;
    private Long count;
}
