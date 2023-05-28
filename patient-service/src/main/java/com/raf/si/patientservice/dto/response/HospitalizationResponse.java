package com.raf.si.patientservice.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class HospitalizationResponse {
    private Long id;
    private Long hospitalRoomId;
    private UUID lbp;
    private UUID doctorLbz;
    private Date receiptDate;
    private String diagnosis;
    private String note;
    private UUID registerLbz;
    private Date dischargeDate;
}
