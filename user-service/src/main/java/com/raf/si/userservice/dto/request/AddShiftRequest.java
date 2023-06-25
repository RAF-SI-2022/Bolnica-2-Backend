package com.raf.si.userservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AddShiftRequest {
    @NotEmpty(message = "Tip smene ne sme da bude prazan")
    private String shiftType;
    @NotNull(message = "Datum ne sme da bude prazan")
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
