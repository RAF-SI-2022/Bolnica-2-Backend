package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.response.HospitalRoomListResponse;
import com.raf.si.patientservice.mapper.HospitalRoomMapper;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.repository.filtering.HospitalRoomRepository;
import com.raf.si.patientservice.service.HospitalRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class HospitalRoomServiceImpl implements HospitalRoomService {

    private final HospitalRoomRepository hospitalRoomRepository;
    private final HospitalRoomMapper hospitalRoomMapper;

    public HospitalRoomServiceImpl(HospitalRoomRepository hospitalRoomRepository,
                                   HospitalRoomMapper hospitalRoomMapper) {

        this.hospitalRoomRepository = hospitalRoomRepository;
        this.hospitalRoomMapper = hospitalRoomMapper;
    }

    @Override
    public HospitalRoomListResponse getHospitalRooms(UUID pbo, Pageable pageable) {
        Page<HospitalRoom> roomsPage = hospitalRoomRepository.findByPbo(pbo, pageable);
        return hospitalRoomMapper.roomsToRoomListResponse(roomsPage);
    }
}
