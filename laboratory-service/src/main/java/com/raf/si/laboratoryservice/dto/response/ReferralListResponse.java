package com.raf.si.laboratoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReferralListResponse {
    private List<ReferralResponse> referrals;
    private Long count;
}
