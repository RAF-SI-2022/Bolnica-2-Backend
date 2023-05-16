package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.http.ReferralResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.HospitalizationMapper;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.model.Hospitalization;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.HospitalRoomRepository;
import com.raf.si.patientservice.repository.HospitalizationRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class HospitalizationServiceTest {

    private HospitalizationRepository hospitalizationRepository;
    private HospitalRoomRepository hospitalRoomRepository;
    private PatientService patientService;
    private HospitalizationMapper hospitalizationMapper;
    private HospitalizationService hospitalizationService;
    private EntityManager entityManager;

    @BeforeEach
    void setup() {
        hospitalizationRepository = mock(HospitalizationRepository.class);
        hospitalRoomRepository = mock(HospitalRoomRepository.class);
        patientService = mock(PatientService.class);
        hospitalizationMapper = new HospitalizationMapper();
        entityManager = mock(EntityManager.class);

        hospitalizationService = new HospitalizationServiceImpl(
                hospitalizationRepository,
                hospitalRoomRepository,
                hospitalizationMapper,
                patientService
        );

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

    private void mockChangeStatus() {
        Mockito.mockStatic(HttpUtils.class);
        ResponseEntity<ReferralResponse> responseMock = mock(ResponseEntity.class);

        when(HttpUtils.changeReferralStatus(any(), any(), any()))
                .thenReturn(responseMock);
    }

    private void mockTokenPayloadUtil() {
        Mockito.mockStatic(TokenPayloadUtil.class);

        TokenPayload tokenPayload = makeTokenPayload();

        when(TokenPayloadUtil.getTokenPayload())
                .thenReturn(tokenPayload);
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

    private TokenPayload makeTokenPayload() {
        TokenPayload tokenPayload = new TokenPayload();

        tokenPayload.setPbo(UUID.randomUUID());
        tokenPayload.setLbz(UUID.fromString("8a8ddcb8-f35b-11ed-a05b-0242ac120003"));

        return tokenPayload;
    }
}
