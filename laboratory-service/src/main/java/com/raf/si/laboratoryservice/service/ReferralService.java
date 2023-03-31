package com.raf.si.laboratoryservice.service;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;

public interface ReferralService {
    ReferralResponse createReferral(CreateReferralRequest createReferralRequest);

    ReferralResponse getReferral(Long id);

    ReferralResponse deleteReferral(Long id);
}
