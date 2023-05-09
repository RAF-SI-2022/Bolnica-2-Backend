package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.response.HospitalRoomListResponse;
import com.raf.si.patientservice.model.HospitalRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HospitalRoomMapper {

    public HospitalRoomListResponse roomsToRoomListResponse(Page<HospitalRoom> roomsPage) {
        return new HospitalRoomListResponse(roomsPage.toList(), roomsPage.getTotalElements());
    }
}
