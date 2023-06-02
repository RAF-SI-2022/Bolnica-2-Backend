package com.raf.si.patientservice.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class HospPatientByHospitalResponse {

    private UUID pbo;
    private String departmentName;
    private Long hospitalRoomId;
    private Integer roomNumber;
    private UUID lbp;
    private String patientFirstName;
    private String patientLastName;
    private Date birthDate;
    private String jmbg;
    private Date receiptDate;
    private String diagnosis;
    private String note;
    private String doctorFirstName;
    private String doctorLastName;
}
