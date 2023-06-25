package com.raf.si.patientservice.dto.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class HospitalizationRequest {
    @NotNull(message = "ID bolnicke sobe ne sme biti prazan")
    private Long hospitalRoomId;

    @NotNull(message = "Lbp ne sme biti prazan")
    private UUID lbp;

    @NotNull(message = "Lbz lekara specijaliste ne sme biti prazan")
    private UUID specialistLbz;

    @NotEmpty(message = "Dijagnoza ne sme biti prazna")
    private String diagnosis;

    private String note;

    //FIXME Po novoj specifikaciji može bez uputa.
    private Long referralId;
}
