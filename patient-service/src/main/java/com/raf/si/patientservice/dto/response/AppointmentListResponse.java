package com.raf.si.patientservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentListResponse {
    private List<AppointmentResponse> appointments;
    private Long count;
}
