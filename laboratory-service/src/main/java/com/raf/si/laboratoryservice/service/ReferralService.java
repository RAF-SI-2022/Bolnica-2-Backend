package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.dto.response.UnprocessedReferralsResponse;
import com.raf.si.laboratoryservice.model.Referral;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ReferralService {
    ReferralResponse createReferral(CreateReferralRequest createReferralRequest);

    ReferralListResponse referralHistory(UUID lbp, Date dateFrom, Date dateTo, Pageable pageable);

    ReferralResponse getReferral(Long id);

    ReferralResponse deleteReferral(Long id);

    List<UnprocessedReferralsResponse> unprocessedReferrals(UUID lbp, String authorizationHeader);
}
