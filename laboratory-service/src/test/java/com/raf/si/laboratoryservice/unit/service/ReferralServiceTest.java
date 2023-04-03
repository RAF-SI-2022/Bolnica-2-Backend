package com.raf.si.laboratoryservice.unit.service;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.mapper.ReferralMapper;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
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
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
class ReferralServiceTest {

    private ReferralServiceImpl referralService;
    private ReferralRepository referralRepository;
    private LabWorkOrderRepository labWorkOrderRepository;
    private ReferralMapper referralMapper;

    private Authentication authentication;

    @BeforeEach
    void setUp() {
        referralRepository = mock(ReferralRepository.class);
        labWorkOrderRepository = mock(LabWorkOrderRepository.class);
        referralMapper = mock(ReferralMapper.class);
        referralService = new ReferralServiceImpl(referralRepository, labWorkOrderRepository, referralMapper);
        authentication = mock(Authentication.class);
    }

    @Test
    void testCreateReferral() {
        CreateReferralRequest createReferralRequest = new CreateReferralRequest();
        Referral referral = new Referral();
        ReferralResponse referralResponse = new ReferralResponse();

        when(referralMapper.requestToModel(createReferralRequest)).thenReturn(referral);
        when(referralRepository.save(referral)).thenReturn(referral);
        when(referralMapper.modelToResponse(referral)).thenReturn(referralResponse);

        // Act
        ReferralResponse result = referralService.createReferral(createReferralRequest);

        assertNotNull(result);
        assertEquals(referralResponse, result);
    }

    @Test
    void testGetReferral() {
        Long referralId = 1L;
        Referral referral = new Referral();
        ReferralResponse referralResponse = new ReferralResponse();

        when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));
        when(referralMapper.modelToResponse(referral)).thenReturn(referralResponse);

        // Act
        ReferralResponse result = referralService.getReferral(referralId);

        assertNotNull(result);
        assertEquals(referralResponse, result);
    }
    @Test
    public void testDeleteReferral_Success() {
        Long referralId = 1L;
        UUID lbzFromToken = UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
        Referral referral = new Referral();
        referral.setId(referralId);
        referral.setLbz(lbzFromToken);

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));
        when(labWorkOrderRepository.findByReferral(referral)).thenReturn(null);

        Referral updatedReferral = new Referral();
        updatedReferral.setId(referralId);
        updatedReferral.setDeleted(true);
        when(referralRepository.save(referral)).thenReturn(updatedReferral);
        ReferralResponse referralResponse = new ReferralResponse();
        when(referralMapper.modelToResponse(updatedReferral)).thenReturn(referralResponse);

        // Act
        ReferralResponse result = referralService.deleteReferral(referralId);

        verify(referralRepository).findById(referralId);
        verify(labWorkOrderRepository).findByReferral(referral);
        verify(referralRepository).save(referral);
        verify(referralMapper).modelToResponse(updatedReferral);
        assertEquals(referralResponse, result);
    }


    @Test
    void testReferralHistory() {
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

        assertNotNull(result);
        assertEquals(referralListResponse, result);
    }

    @Test
    public void testUnprocessedReferrals() {
        UUID lbp = UUID.randomUUID();
        UUID pboFromToken = UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435");
        Referral referral1 = new Referral();
        referral1.setLbp(lbp);
        referral1.setPboReferredTo(pboFromToken);
        referral1.setStatus(ReferralStatus.NEREALIZOVAN);

        Referral referral2 = new Referral();
        referral2.setLbp(lbp);
        referral2.setPboReferredTo(pboFromToken);
        referral2.setStatus(ReferralStatus.NEREALIZOVAN);

        List<Referral> unprocessedReferrals = Arrays.asList(referral1, referral2);
        List<Referral> unprocessedReferralsWithoutLabWorkOrder = Collections.singletonList(referral1);
        ReferralListResponse expectedResponse = new ReferralListResponse();

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setPbo(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(referralRepository.findByLbpAndPboReferredToAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN)).thenReturn(Optional.of(unprocessedReferrals));
        when(labWorkOrderRepository.findByReferral(referral1)).thenReturn(null);
        when(labWorkOrderRepository.findByReferral(referral2)).thenReturn(new LabWorkOrder());
        when(referralMapper.referralListToListResponse(unprocessedReferralsWithoutLabWorkOrder)).thenReturn(expectedResponse);


        ReferralListResponse actualResponse = referralService.unprocessedReferrals(lbp);

        assertEquals(expectedResponse, actualResponse);
        verify(referralRepository, times(1)).findByLbpAndPboReferredToAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN);
        verify(labWorkOrderRepository, times(1)).findByReferral(referral1);
        verify(labWorkOrderRepository, times(1)).findByReferral(referral2);
        verify(referralMapper, times(1)).referralListToListResponse(unprocessedReferralsWithoutLabWorkOrder);
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
