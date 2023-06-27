package com.raf.si.patientservice.unit.controller;

import com.raf.si.patientservice.controller.HospitalRoomController;
import com.raf.si.patientservice.dto.response.HospitalBedAvailabilityResponse;
import com.raf.si.patientservice.dto.response.HospitalRoomListResponse;
import com.raf.si.patientservice.service.HospitalRoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HospitalRoomControllerTest {

    @Mock
    private HospitalRoomService hospitalRoomService;

    @InjectMocks
    private HospitalRoomController hospitalRoomController;

    @Test
    void getHospitalRooms_Success() {
        HospitalRoomListResponse response = new HospitalRoomListResponse();

        when(hospitalRoomService.getHospitalRooms(any(), any()))
                .thenReturn(response);

        assertEquals(hospitalRoomController.getRooms(UUID.randomUUID(), 0, 1),
                ResponseEntity.ok(response));
    }

    @Test
    void getBedAvailabilityInTheRoom_Success() {
        HospitalBedAvailabilityResponse response = new HospitalBedAvailabilityResponse();

        when(hospitalRoomService.getBedAvailability(any()))
                .thenReturn(response);

        ResponseEntity<HospitalBedAvailabilityResponse> expectedResponse =
                ResponseEntity.ok(response);

        assertEquals(expectedResponse, hospitalRoomController.getBedAvailabilityInTheRoom(UUID.randomUUID()));
    }
}
