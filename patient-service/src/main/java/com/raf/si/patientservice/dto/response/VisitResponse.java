package com.raf.si.patientservice.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class VisitResponse {

    private Long id;
    private UUID lbp;
    private UUID registerLbz;
    private Date visitDate;
    private String visitorFirstName;
    private String visitorLastName;
    private String JMBGVisitor;
    private String note;
}
