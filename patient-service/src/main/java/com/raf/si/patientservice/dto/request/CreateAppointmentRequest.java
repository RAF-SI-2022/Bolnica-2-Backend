package com.raf.si.patientservice.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Data
public class CreateAppointmentRequest {
    @NotNull(message = "Lbp ne sme biti prazan")
    private UUID lbp;
    @NotNull(message = "Datum i vreme prijema ne sme biti prazan")
    private Date receiptDate;
    private String note;
}
