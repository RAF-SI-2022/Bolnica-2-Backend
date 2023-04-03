package com.raf.si.laboratoryservice.dto.response;

import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
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
    private ReferralType type;
    private UUID lbz;
    private UUID pboReferredFrom;
    private UUID pboReferredTo;
    private UUID lbp;
    private Date creationTime;
    private ReferralStatus status;
    private String requiredAnalysis;
    private String comment;
    private String referralDiagnosis;
    private String referralReason;
}
