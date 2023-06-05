package com.raf.si.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class TestingRequest {
    private Long scheduledTestingId;
    @NotNull(message = "Razlog ne sme biti prazan")
    private String reason;
    @NotNull(message = "Temperatura ne sme biti prazna")
    private String temperature;
    @NotNull(message = "Krvni pritisak ne sme biti prazan")
    private String bloodPressure;
    @NotNull(message = "Puls ne sme biti prazan")
    private String pulse;
    @NotNull(message = "Primenjene terapije ne smeju biti prazne")
    private String appliedTherapies;
    @NotNull(message = "Opis stanja ne sme biti prazno")
    private String description;
    @NotNull(message = "Datum i vreme prikupljanja informacija ne sme biti prazno")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date collectedInfoDate;
}
