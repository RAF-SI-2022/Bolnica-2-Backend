package com.raf.si.patientservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Data
public class SchedMedExamRequest {

    @NotNull(message = "ID pacijenta ne sme biti prazan")
    private UUID lbp;
    @NotNull(message = "ID doktora kog koga je zakazano ne sme biti prazano")
    private UUID lbzDoctor;
    @NotNull(message = "Datum zakazanog pregleda ne sme biti prazan")
    private Date appointmentDate;
    private String note;
    @NotNull(message = "ID zaposlenog koji je zakazao pregled ne sme biti prazan")
    private UUID lbzNurse;
    private Boolean covid;
}
