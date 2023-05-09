package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.HospitalRoom;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class HospitalRoomListResponse {
    private List<HospitalRoom> rooms;
    private Long count;
}
