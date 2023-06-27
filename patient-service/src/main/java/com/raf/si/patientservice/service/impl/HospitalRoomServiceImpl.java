package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.response.HospitalBedAvailabilityResponse;
import com.raf.si.patientservice.dto.response.HospitalRoomListResponse;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.repository.HospitalRoomRepository;
import com.raf.si.patientservice.service.HospitalRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class HospitalRoomServiceImpl implements HospitalRoomService {

    private final HospitalRoomRepository hospitalRoomRepository;

    public HospitalRoomServiceImpl(HospitalRoomRepository hospitalRoomRepository) {
        this.hospitalRoomRepository = hospitalRoomRepository;
    }

    @Override
    public HospitalRoomListResponse getHospitalRooms(UUID pbo, Pageable pageable) {
        Page<HospitalRoom> roomsPage = hospitalRoomRepository.findByPbo(pbo, pageable);
        return new HospitalRoomListResponse(roomsPage.toList(), roomsPage.getTotalElements());
    }


    public HospitalBedAvailabilityResponse getBedAvailability(UUID pbo) {
        int totalBeds = hospitalRoomRepository.countTotalBedsByDepartment(pbo);
        int bedsInUse = hospitalRoomRepository.countBedsInUseByDepartment(pbo);
        int availableBeds = totalBeds - bedsInUse;
        return new HospitalBedAvailabilityResponse(totalBeds, bedsInUse, availableBeds);
    }
}
