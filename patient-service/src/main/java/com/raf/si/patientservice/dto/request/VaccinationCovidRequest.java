package com.raf.si.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class VaccinationCovidRequest {
    @NotNull
    private Long vaccinationId;
    @NotNull(message = "Polje datum i vreme ne sme biti prazno")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    @NotEmpty
    private String vaccineName;
    @NotNull
    private Long doseReceived;

}
