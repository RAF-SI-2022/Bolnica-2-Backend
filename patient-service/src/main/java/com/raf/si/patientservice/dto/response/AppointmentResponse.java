package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import com.raf.si.patientservice.model.enums.appointment.AppointmentStatus;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class AppointmentResponse {
    private Long id;
    private Date receiptDate;
    private AppointmentStatus status;
    private String note;
    private PatientResponse patient;
    private UserResponse employee;
    private DepartmentResponse department;
}
