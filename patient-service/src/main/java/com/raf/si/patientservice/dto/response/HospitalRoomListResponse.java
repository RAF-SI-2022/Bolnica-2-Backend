package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.HospitalRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HospitalRoomListResponse {
    private List<HospitalRoom> rooms;
    private Long count;
}
