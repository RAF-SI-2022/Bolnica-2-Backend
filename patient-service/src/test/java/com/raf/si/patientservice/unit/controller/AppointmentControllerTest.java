package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.AppointmentController;
import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void createAppointment_Success() {
        CreateAppointmentRequest createAppointmentRequest = new CreateAppointmentRequest();
        AppointmentResponse appointmentResponse = new AppointmentResponse();

        when(appointmentService.createAppointment(createAppointmentRequest, ""))
                .thenReturn(appointmentResponse);

        assertEquals(appointmentController.createAppointment(createAppointmentRequest, ""),
                ResponseEntity.ok(appointmentResponse));
    }

    @Test
    void getAppointments_Success() {
        AppointmentListResponse response = new AppointmentListResponse();

        when(appointmentService.getAppointments(any(), any(), any(), any()))
                .thenReturn(response);

        assertEquals(appointmentController.getAppointments(UUID.randomUUID(), new Date(), "", 0, 1),
                ResponseEntity.ok(response));
    }

    @Test
    void changeAppointmentStatus_Success() {
        AppointmentResponse response = new AppointmentResponse();
        long id = 0;

        when(appointmentService.changeStatus(any(), any(), any()))
                .thenReturn(response);

        assertEquals(appointmentController.changeStatus(id, "", ""),
                ResponseEntity.ok(response));
    }
}
