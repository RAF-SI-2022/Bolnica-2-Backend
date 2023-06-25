package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.DischargeRequest;
import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.MedicalReportRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.DoctorResponse;
import com.raf.si.patientservice.dto.response.http.ReferralResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.HospitalizationMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.repository.filtering.filter.HospitalisedPatientSearchFilter;
import com.raf.si.patientservice.repository.filtering.specification.DischargeSpecification;
import com.raf.si.patientservice.repository.filtering.specification.HospitalisedPatientSpecification;
import com.raf.si.patientservice.repository.filtering.specification.MedicalReportSpecification;
import com.raf.si.patientservice.repository.filtering.specification.PatientConditionSpecification;
import com.raf.si.patientservice.service.HospitalizationService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.impl.HospitalizationServiceImpl;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HospitalizationServiceTest {

    private HospitalizationRepository hospitalizationRepository;
    private HospitalRoomRepository hospitalRoomRepository;
    private PatientConditionRepository patientConditionRepository;
    private PatientService patientService;
    private HospitalizationMapper hospitalizationMapper;
    private HospitalizationService hospitalizationService;
    private EntityManager entityManager;
    private MedicalReportRepository medicalReportRepository;
    private DischargeListRepository dischargeRepository;

    @BeforeEach
    void setup() {
        hospitalizationRepository = mock(HospitalizationRepository.class);
        hospitalRoomRepository = mock(HospitalRoomRepository.class);
        patientConditionRepository = mock(PatientConditionRepository.class);
        patientService = mock(PatientService.class);
        hospitalizationMapper = new HospitalizationMapper();
        entityManager = mock(EntityManager.class);
        medicalReportRepository = mock(MedicalReportRepository.class);
        dischargeRepository = mock(DischargeListRepository.class);

        hospitalizationService = new HospitalizationServiceImpl(
                hospitalizationRepository,
                hospitalRoomRepository,
                hospitalizationMapper,
                patientService,
                patientConditionRepository,
                medicalReportRepository,
                dischargeRepository);

        ReflectionTestUtils.setField(
                hospitalizationService,
                "entityManager",
                entityManager
        );

        when(patientService.findPatient((UUID) any()))
                .thenReturn(makePatient());

        mockChangeStatus();
        mockTokenPayloadUtil();
    }

    @AfterEach
    void cleanup() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void hospitalize_RoomNotFound_ThrowException() {
        when(entityManager.find(any(), any()))
                .thenReturn(null);

        assertThrows(BadRequestException.class,
                () -> hospitalizationService.hospitalize(makeHospitalizationRequest(), ""));
    }

    @Test
    void hospitalize_RoomNotEnoughCapacity_ThrowException() {
        HospitalRoom hospitalRoom = makeHospitalRoom();
        hospitalRoom.setOccupation(1000);

        when(entityManager.find(any(), any()))
                .thenReturn(hospitalRoom);

        assertThrows(BadRequestException.class,
                () -> hospitalizationService.hospitalize(makeHospitalizationRequest(), ""));
    }

    @Test
    void hospitalize_PatientAlreadyHospitalized_ThrowException() {
        HospitalRoom hospitalRoom = makeHospitalRoom();
        Patient patient = makePatient();

        when(entityManager.find(any(), any()))
                .thenReturn(hospitalRoom);
        when(patientService.findPatient((UUID) any()))
                .thenReturn(patient);
        when(hospitalizationRepository.patientAlreadyHospitalized(patient))
                .thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> hospitalizationService.hospitalize(makeHospitalizationRequest(), ""));
    }

    @Test
    void hospitalize_Success() {
        HospitalRoom hospitalRoom = makeHospitalRoom();
        Patient patient = makePatient();
        Hospitalization hospitalization = makeHospitalization(hospitalRoom, patient);

        when(entityManager.find(any(), any()))
                .thenReturn(hospitalRoom);
        when(hospitalizationRepository.patientAlreadyHospitalized(patient))
                .thenReturn(false);

        when(hospitalizationRepository.save(any()))
                .thenReturn(hospitalization);
        when(hospitalRoomRepository.save(hospitalRoom))
                .thenReturn(hospitalRoom);

        assertEquals(hospitalizationService.hospitalize(makeHospitalizationRequest(), ""),
                hospitalizationMapper.hospitalizationToResponse(hospitalization, hospitalRoom, patient));
    }

    @Test
    void getHospitalisedPatients_Success() {
        Patient patient = makePatient();
        HospitalRoom hospitalRoom = makeHospitalRoom();
        Hospitalization hospitalization = makeHospitalization(hospitalRoom, patient);
        Pageable pageable = PageRequest.of(0, 5);
        HospitalisedPatientSearchFilter filter = new HospitalisedPatientSearchFilter(null, hospitalRoom.getPbo(),
                null, null, null, null, null);
        HospitalisedPatientSpecification spec = new HospitalisedPatientSpecification(filter);
        Page<Hospitalization> pages = new PageImpl<>(Collections.singletonList(hospitalization));

        DoctorResponse doctorResponse = new DoctorResponse();
        doctorResponse.setLbz(hospitalization.getDoctorLBZ());
        doctorResponse.setFirstName("Ime");
        doctorResponse.setLastName("Prezime");
        mockFindDoctors(doctorResponse);

        when(hospitalizationRepository.findAll(spec, pageable))
                .thenReturn(pages);

        assertEquals(hospitalizationService.getHospitalisedPatients(null, filter.getPbo(), null,
                null, null, null, null,
                PageRequest.of(0, 5)),
                new HospitalisedPatientsListResponse(Collections.singletonList(hospitalizationMapper.
                        hospitalizationToHospitalisedPatient(hospitalization, Collections.singletonList(doctorResponse))),
                        1L));
    }

    @Test
    void getHospitalisedPatientsByHospital_Success() {
        UUID pbb = UUID.randomUUID();
        Patient patient = makePatient();
        HospitalRoom hospitalRoom = makeHospitalRoom();
        Hospitalization hospitalization = makeHospitalization(hospitalRoom, patient);
        Pageable pageable = PageRequest.of(0, 5);
        HospitalisedPatientSearchFilter filter = new HospitalisedPatientSearchFilter(null, null,
                null, null, null, null, Collections.singletonList(hospitalRoom.getPbo()));
        HospitalisedPatientSpecification spec = new HospitalisedPatientSpecification(filter);
        Page<Hospitalization> pages = new PageImpl<>(Collections.singletonList(hospitalization));

        DoctorResponse doctorResponse = new DoctorResponse();
        doctorResponse.setLbz(hospitalization.getDoctorLBZ());
        doctorResponse.setFirstName("Ime");
        doctorResponse.setLastName("Prezime");
        mockFindDoctors(doctorResponse);
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setPbo(hospitalRoom.getPbo());
        departmentResponse.setName("Name");

        mockFindDepartmentsByHospital(departmentResponse);

        when(hospitalizationRepository.findAll(spec, pageable))
                .thenReturn(pages);

        assertEquals(hospitalizationService.getHospitalisedPatientsByHospital(null, pbb, null,
                null, null, null, null, null, null,
                PageRequest.of(0, 5)),
                new HospPatientByHospitalListResponse(
                        Collections.singletonList(hospitalizationMapper.
                                hospitalizationToHospPatientByHospitalResponse(
                                        hospitalization,
                                        Collections.singletonList(doctorResponse),
                                        Collections.singletonList(departmentResponse))
                        ),
                        1L
                )
        );
    }

    @Test
    void createPatientCondition_WhenAllPropertiesAreNull_ThrowsBadRequestException() {
        UUID lbp = UUID.randomUUID();
        PatientConditionRequest patientConditionRequest = new PatientConditionRequest();

        assertThrows(BadRequestException.class,
                () -> hospitalizationService.createPatientCondition(lbp, patientConditionRequest));
    }

    @Test
    void createPatientCondition_WhenHospitalisedPatientDoesNotExist_ThrowsNotFoundException() {
        UUID lbp = UUID.randomUUID();
        PatientConditionRequest patientConditionRequest = makePatientConditionRequest();

        when(hospitalizationRepository.getHospitalizedPatient(lbp)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> hospitalizationService.createPatientCondition(lbp, patientConditionRequest));
    }

    @Test
    void createPatientCondition_Success() {
        UUID lbp = UUID.randomUUID();
        PatientConditionRequest patientConditionRequest = makePatientConditionRequest();
        Patient patient = makePatient();

        when(hospitalizationRepository.getHospitalizedPatient(lbp)).thenReturn(Optional.of(patient));

        PatientCondition patientCondition = hospitalizationMapper.patientConditionRequestToPatientCondition(patient, makeTokenPayload().getLbz(), patientConditionRequest);

        when(patientConditionRepository.save(any()))
                .thenReturn(patientCondition);

        assertEquals(hospitalizationService.createPatientCondition(lbp, patientConditionRequest),
                hospitalizationMapper.patientConditionToPatientConditionResponse(patientCondition));
    }

    @Test
    void getPatientConditions_Success() {
        UUID lbp = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        when(patientConditionRepository.findAll(any(PatientConditionSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        assertEquals(hospitalizationService.getPatientConditions(lbp, null, null, pageable),
                new PatientConditionListResponse(Collections.emptyList(), 0L));
    }

    @Test
    void createMedicalReport_Success() {
        UUID lbp = UUID.randomUUID();
        Patient patient = makePatient();
        MedicalReportRequest request = makeMedicalReportRequest();

        when(hospitalizationRepository.getHospitalizedPatient(lbp))
                .thenReturn(Optional.of(patient));

        MedicalReport medicalReport = hospitalizationMapper.medicalReportRequestToMedicalReport(
                patient,
                request,
                makeTokenPayload().getLbz(),
                true
        );

        when(medicalReportRepository.save(any()))
                .thenReturn(medicalReport);

        assertEquals(hospitalizationService.createMedicalReport(lbp, request),
                hospitalizationMapper.medicalReportToMedicalReportResponse(medicalReport));
    }

    @Test
    void getMedicalReports_Success() {
        UUID lbp = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        when(medicalReportRepository.findAll(any(MedicalReportSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        assertEquals(hospitalizationService.getMedicalReports(lbp, null, null, null, pageable),
                new MedicalReportListResponse(Collections.emptyList(), 0L));
    }

    @Test
    void createDischarge_WhenHospitalizationDoesNotExist_ThrowNotFoundException() {
        UUID lbp = UUID.randomUUID();

        when(hospitalizationRepository.getHospitalizationByLbp(lbp))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> hospitalizationService.createDischarge(lbp, null, ""));
    }

    @Test
    void createDischarge_Success() {
        UUID lbp = UUID.randomUUID();
        Patient patient = makePatient();
        HospitalRoom hospitalRoom = makeHospitalRoom();
        Hospitalization hospitalization = makeHospitalization(hospitalRoom, patient);

        when(hospitalizationRepository.getHospitalizationByLbp(lbp))
                .thenReturn(Optional.of(hospitalization));
        DoctorResponse doctorResponse = makeDoctorResponse();

        mockGetHeadOfDepartment(doctorResponse);

        DischargeRequest request = new DischargeRequest();
        request.setAnamnesis("anamnesis");
        request.setConclusion("conclusion");

        DischargeList discharge = hospitalizationMapper.dischargeListRequestToModel(request, hospitalization,
                doctorResponse.getLbz(), doctorResponse.getLbz());

        when(dischargeRepository.save(any()))
                .thenReturn(discharge);

        assertEquals(hospitalizationService.createDischarge(lbp, request, UUID.randomUUID().toString()),
                hospitalizationMapper.modelToDischargeResponse(discharge, doctorResponse, doctorResponse));
    }

    @Test
    void getDischarge_Success() {
        UUID lbp = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        when(dischargeRepository.findAll(any(DischargeSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        DoctorResponse doctorResponse = makeDoctorResponse();

        mockFindDoctors(doctorResponse);

        assertEquals(hospitalizationService.getDischarge(lbp, null, null, null, pageable, "token"),
                new DischargeListResponse(Collections.emptyList(), 0L));
    }

    @SuppressWarnings("unchecked")
    private void mockChangeStatus() {
        Mockito.mockStatic(HttpUtils.class);
        ResponseEntity<ReferralResponse> responseMock = mock(ResponseEntity.class);

        when(HttpUtils.changeReferralStatus(any(), any(), any()))
                .thenReturn(responseMock);
    }

    @SuppressWarnings("unchecked")
    private void mockFindDoctors(DoctorResponse doctorResponse) {
        DoctorResponse[] arr = new DoctorResponse[]{doctorResponse};
        ResponseEntity<DoctorResponse[]> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(arr);

        when(HttpUtils.findDoctors(any()))
                .thenReturn(responseEntity);
    }

    @SuppressWarnings("unchecked")
    private void mockFindDepartmentsByHospital(DepartmentResponse departmentResponse) {
        DepartmentResponse[] arr = new DepartmentResponse[]{departmentResponse};
        ResponseEntity<DepartmentResponse[]> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(arr);

        when(HttpUtils.findDepartmentsByHospital(any(), any()))
                .thenReturn(responseEntity);
    }

    @SuppressWarnings("unchecked")
    private void mockGetHeadOfDepartment(DoctorResponse doctorResponse) {
        ResponseEntity<DoctorResponse> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(doctorResponse);

        when(HttpUtils.getHeadOfDepartment(any(), any()))
                .thenReturn(responseEntity);
    }

    private void mockTokenPayloadUtil() {
        Mockito.mockStatic(TokenPayloadUtil.class);

        TokenPayload tokenPayload = makeTokenPayload();

        when(TokenPayloadUtil.getTokenPayload())
                .thenReturn(tokenPayload);
    }

    private MedicalReportRequest makeMedicalReportRequest() {
        MedicalReportRequest request = new MedicalReportRequest();
        request.setObjectiveResult("objectiveResult");
        request.setAdvice("advice");
        request.setDiagnosis("diagnosis");
        request.setConfidentIndicator(true);
        request.setProposedTherapy("proposedTherapy");

        return request;
    }

    private PatientConditionRequest makePatientConditionRequest() {
        PatientConditionRequest request = new PatientConditionRequest();
        request.setDescription("Description");
        request.setAppliedTherapies("Therapies");
        request.setBloodPressure("80/120");
        request.setPulse("70");

        return request;
    }

    private HospitalRoom makeHospitalRoom() {
        HospitalRoom hospitalRoom = new HospitalRoom();
        long id = 1;

        hospitalRoom.setId(id);
        hospitalRoom.setRoomNumber(0);
        hospitalRoom.setOccupation(0);
        hospitalRoom.setCapacity(10);
        hospitalRoom.setRoomName("Room");
        hospitalRoom.setPbo(UUID.randomUUID());
        hospitalRoom.setDescription("Desc");

        return hospitalRoom;
    }

    private Patient makePatient() {
        Patient patient = new Patient();
        long id = 1;

        patient.setId(id);
        patient.setLbp(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        patient.setFirstName("Ime");
        patient.setLastName("Prezime");
        patient.setJmbg("512312311231");
        patient.setBirthDate(new Date());

        return patient;
    }

    private HospitalizationRequest makeHospitalizationRequest() {
        HospitalizationRequest hospitalizationRequest = new HospitalizationRequest();
        long id = 1;

        hospitalizationRequest.setHospitalRoomId(id);
        hospitalizationRequest.setLbp(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        hospitalizationRequest.setDiagnosis("Dijagnoza");
        hospitalizationRequest.setReferralId(id);
        hospitalizationRequest.setSpecialistLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));

        return hospitalizationRequest;
    }

    private Hospitalization makeHospitalization(HospitalRoom hospitalRoom, Patient patient) {
        Hospitalization hospitalization = new Hospitalization();

        hospitalization.setHospitalRoom(hospitalRoom);
        hospitalization.setPatient(patient);
        hospitalization.setDiagnosis("Dijagnoza");
        hospitalization.setDoctorLBZ(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        hospitalization.setReceiptDate(new Date());
        hospitalization.setRegisterLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));

        return hospitalization;
    }

    private DoctorResponse makeDoctorResponse() {
        DoctorResponse doctorResponse = new DoctorResponse();
        TokenPayload tokenPayload = makeTokenPayload();
        doctorResponse.setLbz(tokenPayload.getLbz());
        doctorResponse.setFirstName(tokenPayload.getFirstName());
        doctorResponse.setLastName(tokenPayload.getLastName());

        return doctorResponse;
    }

    private TokenPayload makeTokenPayload() {
        TokenPayload tokenPayload = new TokenPayload();

        tokenPayload.setPbo(UUID.randomUUID());
        tokenPayload.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));
        tokenPayload.setPermissions(List.of("ROLE_DR_SPEC_POV"));

        return tokenPayload;
    }
}
