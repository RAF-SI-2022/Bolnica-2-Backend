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
public class ReferralResponse {
    private Long id;
    private UUID lbz;
    private UUID pboReferredFrom;
    private UUID pboReferredTo;
    private UUID lbp;
    private Date creationTime;
    private String requiredAnalysis;
    private String comment;
    private String referralDiagnosis;
    private String referralReason;
    private Boolean deleted;
}
