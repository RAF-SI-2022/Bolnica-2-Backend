package com.raf.si.patientservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TimeRequest {
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
}
