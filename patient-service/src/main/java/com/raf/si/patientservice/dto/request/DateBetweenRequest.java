package com.raf.si.patientservice.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class DateBetweenRequest {
    private Date startDate;
    private Date endDate;
}
