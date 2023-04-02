package com.raf.si.laboratoryservice.mapper;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.model.Referral;
import org.apache.catalina.mapper.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReferralMapper {

    public Referral requestToModel(CreateReferralRequest createReferralRequest) {
       Referral referral = new Referral();

       referral.setType(createReferralRequest.getType());
       referral.setLbz(createReferralRequest.getLbz());
       referral.setPboReferredFrom(createReferralRequest.getPboReferredFrom());
       referral.setPboReferredTo(createReferralRequest.getPboReferredTo());
       referral.setLbz(createReferralRequest.getLbz());
       referral.setLbp(createReferralRequest.getLbp());
       referral.setCreationTime(createReferralRequest.getCreationTime());
       referral.setReferralReason(createReferralRequest.getReferralReason());
       referral.setReferralDiagnosis(createReferralRequest.getReferralDiagnosis());
       referral.setComment(createReferralRequest.getComment());
       referral.setRequiredAnalysis(createReferralRequest.getRequiredAnalysis());

        return referral;
    }

    public ReferralResponse modelToResponse(Referral referral) {
        ReferralResponse referralResponse = new ReferralResponse();

        referralResponse.setType(referral.getType());
        referralResponse.setPboReferredFrom(referral.getPboReferredFrom());
        referralResponse.setPboReferredTo(referral.getPboReferredTo());
        referralResponse.setLbz(referral.getLbz());
        referralResponse.setLbp(referral.getLbp());
        referralResponse.setCreationTime(referral.getCreationTime());
        referralResponse.setReferralDiagnosis(referral.getReferralDiagnosis());
        referralResponse.setReferralReason(referral.getReferralReason());

        return referralResponse;
    }

    public ReferralListResponse referralPageToReferralListResponse(Page<Referral> referralPage) {
        List<ReferralResponse> referrals = referralPage.getContent()
                .stream()
                .map(this::modelToResponse)
                .collect(Collectors.toList());
        return new ReferralListResponse(referrals, referralPage.getTotalElements());
    }

}
