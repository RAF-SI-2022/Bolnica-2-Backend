package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NotFound;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AppointmentMapper {

    private final PatientMapper patientMapper;

    public AppointmentMapper(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

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

    public AppointmentResponse appointmentToAppointmentResponse(Appointment appointment,
                                                                DepartmentResponse departmentResponse,
                                                                UserResponse employeeResponse) {

        AppointmentResponse response = new AppointmentResponse();

        response.setPatient(patientMapper.patientToPatientResponse(appointment.getPatient()));
        response.setDepartment(departmentResponse);
        response.setEmployee(employeeResponse);

        response.setId(appointment.getId());
        response.setStatus(appointment.getStatus());
        response.setNote(appointment.getNote());
        response.setReceiptDate(appointment.getReceiptDate());

        return response;
    }

    public AppointmentListResponse appointmentsToAppointmentListResponse(List<Appointment> appointments,
                                                                         Long count,
                                                                         DepartmentResponse departmentResponse,
                                                                         Map<UUID, UserResponse> lbzToUserResponse) {

        List<AppointmentResponse> responses = new ArrayList<>();
        for (Appointment appointment : appointments) {
            UserResponse userResponse = lbzToUserResponse.get(appointment.getEmployeeLBZ());

            checkUser(userResponse, appointment.getEmployeeLBZ());

            AppointmentResponse response = appointmentToAppointmentResponse(
              appointment,
              departmentResponse,
              userResponse
            );
            responses.add(response);
        }

        return new AppointmentListResponse(responses, count);
    }

    private void checkUser(UserResponse userResponse, UUID lbz) {
        if (userResponse == null) {
            String errMessage = String.format("Korisnik sa lbz-om %s ne postoji", lbz);
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
    }
}
