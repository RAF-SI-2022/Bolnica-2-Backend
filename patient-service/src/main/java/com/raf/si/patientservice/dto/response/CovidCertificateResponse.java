package com.raf.si.patientservice.dto.response;

import com.raf.si.patientservice.model.enums.certificate.CovidCertificateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CovidCertificateResponse {

    private Long id;
    private UUID certificateNumber;
    private Long vaccinationCovidId;
    private Long testingId;
    private CovidCertificateType covidCertificateType;
    private LocalDateTime dateApply;
    private LocalDateTime endDate;
    private LocalDateTime dateOfIssue;
}
