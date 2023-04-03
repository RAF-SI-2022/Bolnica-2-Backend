package com.raf.si.laboratoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralListResponse {
    private List<ReferralResponse> referrals;
    private Long count;
}
