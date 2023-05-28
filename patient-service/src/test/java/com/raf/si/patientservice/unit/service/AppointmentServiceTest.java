package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.CreateAppointmentRequest;
import com.raf.si.patientservice.dto.response.AppointmentListResponse;
import com.raf.si.patientservice.dto.response.AppointmentResponse;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.AppointmentMapper;
import com.raf.si.patientservice.mapper.PatientMapper;
import com.raf.si.patientservice.model.Appointment;
import com.raf.si.patientservice.model.HealthRecord;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import com.raf.si.patientservice.repository.AppointmentRepository;
import com.raf.si.patientservice.service.AppointmentService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.impl.AppointmentServiceImpl;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class AppointmentServiceTest {

    private AppointmentRepository appointmentRepository;
    private PatientService patientService;
    private AppointmentMapper appointmentMapper;
    private AppointmentService appointmentService;

    @BeforeEach
    void setup() {
        appointmentRepository = mock(AppointmentRepository.class);
        patientService = mock(PatientService.class);
        appointmentMapper = new AppointmentMapper(new PatientMapper());

        appointmentService = new AppointmentServiceImpl(
                appointmentRepository,
                patientService,
                appointmentMapper
        );

        when(patientService.findPatient((UUID) any()))
                .thenReturn(makePatient());

        mockTokenPayloadUtil();
    }

    @AfterEach
    void cleanup() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void createAppointment_RequestedDateInThePast_ThrowException() {
        CreateAppointmentRequest request = makeCreateAppointmentRequest();
        Date pastDate = DateUtils.addDays(new Date(), -1);
        request.setReceiptDate(pastDate);

        assertThrows(BadRequestException.class,
                () -> appointmentService.createAppointment(request, ""));
    }

    @Test
    void createAppointment_PatientHasAppointmentTheSameDay_ThrowException() {
        CreateAppointmentRequest request = makeCreateAppointmentRequest();

        when(appointmentRepository.patientHasAppointmentDateBetween(any(), any(), any()))
                .thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> appointmentService.createAppointment(request, ""));
    }

    @Test
    void createAppointment_Success() {
        CreateAppointmentRequest request = makeCreateAppointmentRequest();
        Patient patient = makePatient();
        Appointment appointment = makeAppointment(patient);

        when(appointmentRepository.patientHasAppointmentDateBetween(any(), any(), any()))
                .thenReturn(false);

        mockHttpUtils();

        when(appointmentRepository.save(any()))
                .thenReturn(appointment);

        assertEquals(makeAppointmentResponse(appointment),
                appointmentService.createAppointment(request, ""));
    }

    @Test
    void changeStatus_AppointmentNotFound_ThrowsException() {
        long id = 1;

        when(appointmentRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class ,
                () -> appointmentService.changeStatus(id, "", ""));
    }

    @Test
    void changeStatus_StatusNotFound_ThrowsException() {
        long id = 1;

        when(appointmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(makeAppointment(makePatient())));

        assertThrows(BadRequestException.class ,
                () -> appointmentService.changeStatus(id, "", ""));
    }

    @Test
    void changeStatus_Success() {
        long id = 1;
        CreateAppointmentRequest request = makeCreateAppointmentRequest();
        Patient patient = makePatient();
        Appointment appointment = makeAppointment(patient);

        when(appointmentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(makeAppointment(makePatient())));

        mockHttpUtils();

        when(appointmentRepository.save(any()))
                .thenReturn(appointment);

        assertEquals(makeAppointmentResponse(appointment),
                appointmentService.changeStatus(id, "Otkazan", ""));
    }

    @Test
    void getAppointments_Success() {
        Patient patient = makePatient();
        Appointment appointment = makeAppointment(patient);

        List<Appointment> appointmentList = Arrays.asList(new Appointment[] {appointment});
        Page<Appointment> appointmentPage = new PageImpl<>(appointmentList);
        long listSize = appointmentList.size();

        when(patientService.findPatient((UUID) any()))
                .thenReturn(patient);
        when(appointmentRepository.findAll((Specification<Appointment>) any(), (Pageable) any()))
                .thenReturn(appointmentPage);

        mockHttpUtils();

        UserResponse userResponse = new UserResponse();
        userResponse.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));

        Map<UUID, UserResponse> lbzToUserResponse = new HashMap<>();
        lbzToUserResponse.put(userResponse.getLbz(), userResponse);

        AppointmentListResponse response = appointmentMapper.appointmentsToAppointmentListResponse(
                appointmentList,
                listSize,
                null,
                lbzToUserResponse
        );

        assertEquals(response,
                appointmentService.getAppointments(UUID.randomUUID(), null, "", PageRequest.of(0, 1)));
    }

    private void mockHttpUtils() {
        Mockito.mockStatic(HttpUtils.class);
        ResponseEntity<UserResponse> userResponse = mock(ResponseEntity.class);
        ResponseEntity<DepartmentResponse> departmentResponse = mock(ResponseEntity.class);

        UserResponse user = new UserResponse();
        user.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));

        List<UserResponse> userResponseList = new ArrayList<>();
        userResponseList.add(user);

        when(HttpUtils.findUserByLbz(any(), any()))
                .thenReturn(userResponse);
        when(HttpUtils.findDepartmentByPbo(any(), any()))
                .thenReturn(departmentResponse);
        when(HttpUtils.findUsersByLbzList(any(), any()))
                .thenReturn(userResponseList);
    }

    private CreateAppointmentRequest makeCreateAppointmentRequest() {
        CreateAppointmentRequest request = new CreateAppointmentRequest();

        request.setLbp(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            request.setReceiptDate(formatter.parse("12-12-2100"));
        }catch(Exception e){
            return null;
        }
        request.setNote("note");

        return request;
    }

    private Appointment makeAppointment(Patient patient) {
        Appointment appointment = new Appointment();

        appointment.setPatient(makePatient());
        appointment.setPbo(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        appointment.setStatus(AppointmentStatus.ZAKAZAN);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            appointment.setReceiptDate(formatter.parse("12-12-2100"));
        }catch(Exception e){
            return null;
        }
        appointment.setEmployeeLBZ(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        appointment.setPatient(patient);

        return appointment;
    }

    private AppointmentResponse makeAppointmentResponse(Appointment appointment) {
        return appointmentMapper.appointmentToAppointmentResponse(appointment, null, null);
    }

    private Patient makePatient() {
        Patient patient = new Patient();
        long id = 1;

        patient.setId(id);
        patient.setLbp(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        patient.setHealthRecord(new HealthRecord());

        return patient;
    }

    private void mockTokenPayloadUtil() {
        Mockito.mockStatic(TokenPayloadUtil.class);

        TokenPayload tokenPayload = makeTokenPayload();

        when(TokenPayloadUtil.getTokenPayload())
                .thenReturn(tokenPayload);
    }

    private TokenPayload makeTokenPayload() {
        TokenPayload tokenPayload = new TokenPayload();

        tokenPayload.setPbo(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        tokenPayload.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));

        return tokenPayload;
    }
}
