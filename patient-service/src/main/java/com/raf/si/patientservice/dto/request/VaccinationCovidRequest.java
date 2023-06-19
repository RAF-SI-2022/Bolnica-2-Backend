package com.raf.si.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class VaccinationCovidRequest {
    @NotNull
    private Long vaccinationId;
    @NotNull
    private Long healthRecordId;
    @NotNull(message = "Polje datum i vreme ne sme biti prazno")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    @NotEmpty
    private String vaccineName;
    @NotEmpty
    private String doseReceived;

}
