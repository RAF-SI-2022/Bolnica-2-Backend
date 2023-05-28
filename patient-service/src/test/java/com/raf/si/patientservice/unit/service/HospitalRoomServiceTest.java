package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.dto.response.HospitalRoomListResponse;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.repository.HospitalRoomRepository;
import com.raf.si.patientservice.service.HospitalRoomService;
import com.raf.si.patientservice.service.impl.HospitalRoomServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HospitalRoomServiceTest {

    @Mock
    private HospitalRoomRepository hospitalRoomRepository;

    @InjectMocks
    private HospitalRoomServiceImpl hospitalRoomService;

    @Test
    void getHospitalRooms_Success() {
        HospitalRoom hospitalRoom = new HospitalRoom();
        List<HospitalRoom> rooms = Arrays.asList(new HospitalRoom[] {hospitalRoom});
        Page<HospitalRoom> roomsPage = new PageImpl<>(rooms);
        Pageable pageable = PageRequest.of(0, 1);
        long roomCount = rooms.size();

        when(hospitalRoomRepository.findByPbo(any(), any()))
                .thenReturn(roomsPage);

        assertEquals(hospitalRoomService.getHospitalRooms(UUID.randomUUID(), pageable),
                new HospitalRoomListResponse(rooms, roomCount));
    }
}
