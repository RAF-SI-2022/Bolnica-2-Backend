package com.raf.si.patientservice.dto.request;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class VisitRequest {

    @NotEmpty(message = "Ime posetioca ne moze biti prazno")
    private String visitorFirstName;
    @NotEmpty(message = "Prezime posetioca ne moze biti prazno")
    private String visitorLastName;
    @NotEmpty(message = "JMBG posetioca ne moze biti prazno")
    @Pattern(regexp = "^[0-9]*$", message = "Invalidan format JMBG-a")
    private String jmbgVisitor;
    private String note;
}
