package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.response.HospitalRoomListResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HospitalRoomService {
    HospitalRoomListResponse getHospitalRooms(UUID pbo, Pageable pageable);
}
