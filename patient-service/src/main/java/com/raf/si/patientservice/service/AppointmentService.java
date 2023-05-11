package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.model.Appointment;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.UUID;

public interface AppointmentService {
    AppointmentResponse createAppointment(CreateAppointmentRequest request, String token);
    AppointmentListResponse getAppointments(UUID lbp, Date date, String token, Pageable pageable);
    AppointmentResponse changeStatus(Long id, String status, String token);
    Appointment getAppointment(Long id);
}
