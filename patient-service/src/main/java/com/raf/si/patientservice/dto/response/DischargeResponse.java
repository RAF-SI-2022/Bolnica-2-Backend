package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.dto.response.http.DoctorResponse;
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
public class DischargeResponse {

    private Long id;
    private UUID lbp;
    private Long hospitalizationId;
    private Date receiptDate;
    private Date dischargeDate;
    private String diagnosis;
    private String attendDiagnoses;
    private String anamnesis;
    private String courseDisease;
    private String conclusion;
    private String therapy;
    private DoctorResponse doctor;
    private DoctorResponse headOfDepartment;
    private Date date;

}
