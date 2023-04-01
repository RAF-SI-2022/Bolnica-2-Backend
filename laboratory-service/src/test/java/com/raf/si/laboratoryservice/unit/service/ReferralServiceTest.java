package com.raf.si.laboratoryservice.unit.service;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.mapper.ReferralMapper;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.service.impl.ReferralServiceImpl;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
class ReferralServiceTest {

    private ReferralServiceImpl referralService;
    private ReferralRepository referralRepository;
    private ReferralMapper referralMapper;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        referralRepository = mock(ReferralRepository.class);
        referralMapper = mock(ReferralMapper.class);
        referralService = new ReferralServiceImpl(referralRepository, referralMapper);
        authentication = mock(Authentication.class);
    }

    @Test
    void testCreateReferral() {
        // Arrange
        CreateReferralRequest createReferralRequest = new CreateReferralRequest();
        Referral referral = new Referral();
        ReferralResponse referralResponse = new ReferralResponse();

        when(referralMapper.requestToModel(createReferralRequest)).thenReturn(referral);
        when(referralRepository.save(referral)).thenReturn(referral);
        when(referralMapper.modelToResponse(referral)).thenReturn(referralResponse);

        // Act
        ReferralResponse result = referralService.createReferral(createReferralRequest);

        // Assert
        assertNotNull(result);
        assertEquals(referralResponse, result);
    }

    @Test
    void testGetReferral() {
        // Arrange
        Long referralId = 1L;
        Referral referral = new Referral();
        ReferralResponse referralResponse = new ReferralResponse();

        when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));
        when(referralMapper.modelToResponse(referral)).thenReturn(referralResponse);

        // Act
        ReferralResponse result = referralService.getReferral(referralId);

        // Assert
        assertNotNull(result);
        assertEquals(referralResponse, result);
    }

    @Test
    void testDeleteReferral() {
        // Arrange
        Long referralId = 1L;
        UUID lbzFromToken = UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
        Referral referral = new Referral();
        referral.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        ReferralResponse referralResponse = new ReferralResponse();
        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(lbzFromToken);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(tokenPayload);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));
        when(referralRepository.save(referral)).thenReturn(referral);
        when(referralMapper.modelToResponse(referral)).thenReturn(referralResponse);

        // Act
        ReferralResponse result = referralService.deleteReferral(referralId);

        // Assert
        assertNotNull(result);
        assertTrue(referral.isDeleted());
        assertEquals(referralResponse, result);
    }


    @Test
    void testReferralHistory() {
        // Arrange
        UUID lbp = UUID.randomUUID();
        Timestamp dateFrom = new Timestamp(0);
        Timestamp dateTo = new Timestamp(System.currentTimeMillis());
        Pageable pageable = PageRequest.of(0, 10);
        Referral referral = new Referral();
        Page<Referral> referralPage = new PageImpl<>(Collections.singletonList(referral));

        ReferralListResponse referralListResponse = createReferralListResponse();

        when(referralRepository.findByLbpAndCreationTimeBetweenAndDeletedFalse(lbp, dateFrom, dateTo, pageable))
                .thenReturn(referralPage);
        when(referralMapper.referralPageToReferralListResponse(referralPage)).thenReturn(referralListResponse);

        // Act
        ReferralListResponse result = referralService.referralHistory(lbp, dateFrom, dateTo, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(referralListResponse, result);
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
