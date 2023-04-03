package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface ReferralService {
    ReferralResponse createReferral(CreateReferralRequest createReferralRequest);

    ReferralListResponse referralHistory(UUID lbp, Timestamp dateFrom, Timestamp dateTo, Pageable pageable);

    ReferralResponse getReferral(Long id);

    ReferralResponse deleteReferral(Long id);

    ReferralListResponse unprocessedReferrals(UUID lbp);
}
