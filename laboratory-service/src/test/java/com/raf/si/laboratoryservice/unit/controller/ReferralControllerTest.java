package com.raf.si.laboratoryservice.unit.controller;


import com.raf.si.laboratoryservice.controller.ReferralController;
import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.dto.response.UnprocessedReferralsResponse;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import com.raf.si.laboratoryservice.service.ReferralService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReferralControllerTest {

    private ReferralController referralController;
    private ReferralService referralService;

    @BeforeEach
    public void setUp() {
        referralService = mock(ReferralService.class);
        referralController = new ReferralController(referralService);
    }

    @Test
    public void createReferral_Success() {
        CreateReferralRequest createReferralRequest = createReferralRequest();

        ReferralResponse referralResponse = new ReferralResponse();
        referralResponse.setType(ReferralType.LABORATORIJA);
        referralResponse.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        when(referralService.createReferral(createReferralRequest)).thenReturn(referralResponse);

        ResponseEntity<ReferralResponse> responseEntity = referralController.createReferral(createReferralRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(referralResponse, responseEntity.getBody());
    }


    @Test
    public void referralHistory_Success() {
        UUID lbp = UUID.randomUUID();
        Timestamp dateFrom = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        Timestamp dateTo = Timestamp.valueOf(LocalDateTime.now());
        int page = 0;
        int size = 10;
        ReferralListResponse referralListResponse = createReferralListResponse();
        when(referralService.referralHistory(lbp, dateFrom, dateTo, PageRequest.of(page, size)))
                .thenReturn(referralListResponse);

        assertEquals(referralController.referralHistory(lbp, dateFrom, dateTo, page, size),
                ResponseEntity.of(Optional.of(referralListResponse)));
    }

    @Test
    public void getReferral_Success() {
        Long id = 1L;
        ReferralResponse referralResponse = new ReferralResponse();
        when(referralService.getReferral(id)).thenReturn(referralResponse);

        ResponseEntity<ReferralResponse> responseEntity = referralController.getReferral(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(referralResponse, responseEntity.getBody());
    }

    @Test
    public void deleteReferral_Success() {
        Long id = 1L;
        ReferralResponse referralResponse = new ReferralResponse();
        when(referralService.deleteReferral(id)).thenReturn(referralResponse);

        ResponseEntity<ReferralResponse> responseEntity = referralController.deleteReferral(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(referralResponse, responseEntity.getBody());
    }

    @Test
    public void unprocessedReferrals_Success() {
        UUID lbp = UUID.fromString("d79f77be-0a0e-4e2f-88a5-5f5d5cdd1e2c");
        List<UnprocessedReferralsResponse> expected = new ArrayList<>();
        List<UnprocessedReferralsResponse> referralsResponses = new ArrayList<>();
        String token = "Bearer woauhruoawbhfupaw";
        when(referralService.unprocessedReferrals(lbp, token)).thenReturn(referralsResponses);

        ResponseEntity<List<UnprocessedReferralsResponse>> responseEntity = referralController.unprocessedReferrals(lbp, token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expected, responseEntity.getBody());
    }

    private CreateReferralRequest createReferralRequest() {
        CreateReferralRequest createReferralRequest = new CreateReferralRequest();

        createReferralRequest.setType(String.valueOf(ReferralType.LABORATORIJA));
        createReferralRequest.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        return createReferralRequest;
    }


    private ReferralListResponse createReferralListResponse() {
        List<ReferralResponse> referralResponses = new ArrayList<>();
        ReferralResponse referral1 = new ReferralResponse();
        referral1.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        ReferralResponse referral2 = new ReferralResponse();
        referral2.setLbz(UUID.fromString("d79f77be-0a0e-4e2f-88a5-5f5d5cdd1e2c"));

        referralResponses.add(referral1);
        referralResponses.add(referral2);
        ReferralListResponse referralListResponse = new ReferralListResponse(referralResponses, 2L);

        return referralListResponse;
    }
}
