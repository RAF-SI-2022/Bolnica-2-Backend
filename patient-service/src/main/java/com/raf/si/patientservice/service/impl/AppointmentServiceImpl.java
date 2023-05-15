package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.request.UUIDListRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.mapper.AppointmentMapper;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import com.raf.si.patientservice.repository.AppointmentRepository;
import com.raf.si.patientservice.repository.filtering.filter.AppointmentFilter;
import com.raf.si.patientservice.repository.filtering.specification.AppointmentSpecification;
import com.raf.si.patientservice.service.AppointmentService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final AppointmentMapper appointmentMapper;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientService patientService,
                                  AppointmentMapper appointmentMapper) {

        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public AppointmentResponse createAppointment(CreateAppointmentRequest request, String token) {
        Patient patient = patientService.findPatient(request.getLbp());
        checkDate(request.getReceiptDate());
        checkPatientHasAppointmentThatDay(patient, request.getReceiptDate());

        Appointment appointment = appointmentMapper.createAppointmentRequestToAppointment(
                request,
                patient
        );

        UserResponse employeeResponse = findUser(appointment.getEmployeeLBZ(), token);
        DepartmentResponse departmentResponse = findDepartment(appointment.getPbo(), token);

        appointment = appointmentRepository.save(appointment);

        log.info("Zakazan termin prijema sa id-jem " + appointment.getId());
        return appointmentMapper.appointmentToAppointmentResponse(appointment, departmentResponse, employeeResponse);
    }

    @Override
    public AppointmentListResponse getAppointments(UUID lbp, Date date, String token, Pageable pageable) {
        Patient patient = lbp == null? null: patientService.findPatient(lbp);
        UUID pbo = TokenPayloadUtil.getTokenPayload().getPbo();

        AppointmentFilter filter = new AppointmentFilter(
                patient,
                date,
                pbo
        );
        AppointmentSpecification spec = new AppointmentSpecification(filter);

        Page<Appointment> appointments = appointmentRepository.findAll(spec, pageable);
        List<Appointment> appointmentList = appointments.toList();
        DepartmentResponse departmentResponse = findDepartment(pbo, token);
        Map<UUID, UserResponse> lbzToUserResponse = findUsersByLbzList(appointmentList, token);

        return appointmentMapper.appointmentsToAppointmentListResponse(
                appointmentList,
                appointments.getTotalElements(),
                departmentResponse,
                lbzToUserResponse
        );
    }

    @Override
    public AppointmentResponse changeStatus(Long id, String status, String token) {
        Appointment appointment = getAppointment(id);

        AppointmentStatus appointmentStatus = AppointmentStatus.valueOfNotation(status);
        if (appointmentStatus == null) {
            String errMessage = String.format("Status \'%s\' ne postoji", status);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }

        UserResponse employeeResponse = findUser(appointment.getEmployeeLBZ(), token);
        DepartmentResponse departmentResponse = findDepartment(appointment.getPbo(), token);

        appointment.setStatus(appointmentStatus);
        appointment = appointmentRepository.save(appointment);

        log.info(String.format("Promenjen status pregleda sa id-jem %d na %s", id, status));
        return appointmentMapper.appointmentToAppointmentResponse(appointment, departmentResponse, employeeResponse);
    }

    @Override
    public Appointment getAppointment(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    String errMessage = String.format("Zakazana poseta sa id-jem %d ne postoji", id);
                    log.error(errMessage);
                    throw new BadRequestException(errMessage);
                });
    }

    private void checkDate(Date date){
        Date currDate = new Date();
        if (currDate.after(date)) {
            String errMessage = String.format("Datum %s je u proslosti", date.toString());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void checkPatientHasAppointmentThatDay(Patient patient, Date appointmentDate) {
        Date startDate = DateUtils.truncate(appointmentDate, Calendar.DAY_OF_MONTH);
        Date endDate = DateUtils.addDays(startDate, 1);

        if (appointmentRepository.patientHasAppointmentDateBetween(patient, startDate, endDate)) {
            String errMessage = String.format("Pacijent sa lbp-om %s vec ima zakazanu posetu tog dana", patient.getLbp());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private UserResponse findUser(UUID lbz, String token) {
        try {
            return HttpUtils.findUserByLbz(token, lbz).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BadRequestException(e.getMessage());
            }
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private List<UserResponse> findUsers(List<UUID> lbzList, String token) {
        try {
            return HttpUtils.findUsersByLbzList(new UUIDListRequest(lbzList), token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private Map<UUID, UserResponse> findUsersByLbzList(List<Appointment> appointments, String token) {
        Set<UUID> lbzSet = appointments.stream()
                .map(Appointment::getEmployeeLBZ)
                .collect(Collectors.toSet());

        List<UserResponse> userResponses = findUsers(new ArrayList<>(lbzSet), token);

        Map<UUID, UserResponse> lbzToUserResponse = new HashMap<>();
        for (UserResponse userResponse : userResponses) {
            lbzToUserResponse.putIfAbsent(userResponse.getLbz(), userResponse);
        }

        return lbzToUserResponse;
    }

    private DepartmentResponse findDepartment(UUID pbo, String token) {
        try {
            return HttpUtils.findDepartmentByPbo(pbo, token).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new BadRequestException(e.getMessage());
            }
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
