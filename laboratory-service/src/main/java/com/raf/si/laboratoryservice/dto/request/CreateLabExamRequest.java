package com.raf.si.laboratoryservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabExamRequest {
    @NotNull(message = "Polje lbp ne sme biti prazno")
    private UUID lbp;
    @NotNull(message = "Polje datum ne sme biti prazno")
    private Date scheduledDate;
    @NotEmpty(message = "Polje napomena ne sme biti prazno")
    private String note;


}
