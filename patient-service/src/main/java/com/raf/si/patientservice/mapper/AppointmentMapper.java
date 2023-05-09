package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppointmentMapper {

    public Appointment createAppointmentRequestToAppointment(CreateAppointmentRequest request,
                                                             Patient patient) {

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setNote(request.getNote());
        appointment.setReceiptDate(request.getReceiptDate());

        TokenPayload token = TokenPayloadUtil.getTokenPayload();
        appointment.setPbo(token.getPbo());
        appointment.setEmployeeLBZ(token.getLbz());

        return appointment;
    }

    public AppointmentResponse appointmentToAppointmentResponse(Appointment appointment) {

        AppointmentResponse response = new AppointmentResponse();

        response.setLbp(appointment.getPatient().getLbp());

        response.setId(appointment.getId());
        response.setPbo(appointment.getPbo());
        response.setStatus(appointment.getStatus());
        response.setNote(appointment.getNote());
        response.setReceiptDate(appointment.getReceiptDate());
        response.setEmployeeLBZ(appointment.getEmployeeLBZ());

        return response;
    }

    public AppointmentListResponse appointmentsToAppointmentListResponse(Page<Appointment> appointmentPage) {
        List<AppointmentResponse> appointments = appointmentPage.toList()
                .stream()
                .map(this::appointmentToAppointmentResponse)
                .collect(Collectors.toList());

        return new AppointmentListResponse(appointments, appointmentPage.getTotalElements());
    }
}
