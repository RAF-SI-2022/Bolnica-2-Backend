package com.raf.si.laboratoryservice.dto.request;

import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReferralRequest {
    @NotEmpty(message = "Polje uput ne sme biti prazno")
    private ReferralType type;
    @NotEmpty(message = "Polje lbz ne sme biti prazno")
    private UUID lbz;
    @NotEmpty(message = "Polje iz odeljenja ne sme biti prazno")
    private UUID pboReferredFrom;
    @NotEmpty(message = "Polje za odeljenje ne sme biti prazno")
    private UUID pboReferredTo;
    @NotEmpty(message = "Polje lbp ne sme biti prazno")
    private UUID lbp;
    @NotEmpty(message = "Polje datum ne sme biti prazno")
    private Timestamp creationTime;
    private String requiredAnalysis;
    private String comment;
    private String referralDiagnosis;
    private String referralReason;
}
