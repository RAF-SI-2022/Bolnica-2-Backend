package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.HospitalizationMapper;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.repository.HospitalRoomRepository;
import com.raf.si.patientservice.repository.HospitalizationRepository;
import com.raf.si.patientservice.service.HospitalizationService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.service.impl.HospitalizationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;

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
    }

    @AfterEach
    void cleanup() {
        Mockito.framework().clearInlineMocks();
    }

    @Test
    void hospitalize_RoomNotFound() {
        when(entityManager.find(any(), any()))
                .thenReturn(null);

        assertThrows(BadRequestException.class,
                () -> hospitalizationService.hospitalize(new HospitalizationRequest(), ""));
    }
}
