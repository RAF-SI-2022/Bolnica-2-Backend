package com.raf.si.patientservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ScheduledTestingRequest {
    @NotNull(message = "Polje datum i vreme ne sme biti prazno")
    private Date dateAndTime;
    private String note;
}
