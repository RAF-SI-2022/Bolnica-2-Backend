package com.raf.si.laboratoryservice.unit.service;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.*;
import com.raf.si.laboratoryservice.mapper.ReferralMapper;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.service.ReferralService;
import com.raf.si.laboratoryservice.service.impl.ReferralServiceImpl;
import com.raf.si.laboratoryservice.utils.HttpUtils;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        createReferralRequest.setCreationTime(DateUtils.addDays(new Date(), 1));
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
        UUID lbp = UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8");
        Date dateFrom = new Date();
        Date dateTo = new Date();
        Pageable pageable = PageRequest.of(0, 10);

        Referral referral = new Referral();
        referral.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        Page<Referral> referralPage = new PageImpl<>(Collections.singletonList(referral));

        ReferralListResponse referralListResponse = createReferralListResponse();

        when(referralRepository.findByLbpAndCreationTimeBetweenAndDeletedFalse(eq(lbp), eq(dateFrom), any(Date.class), eq(pageable))).thenReturn(referralPage);
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
        String token = "Bearer test";
        Referral referral1 = new Referral();
        referral1.setLbp(lbp);
        referral1.setPboReferredFrom(pboFromToken);
        referral1.setStatus(ReferralStatus.NEREALIZOVAN);
        referral1.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        referral1.setCreationTime(new Date());
        referral1.setRequiredAnalysis("Krvna slika");
        referral1.setComment("Komentar");

        List<Referral> unprocessedReferrals = Arrays.asList(referral1);

        mockConnectionWithUserService_Doctors();
        mockConnectionWithUserService_Departments();

        List<UnprocessedReferralsResponse> expectedResponse = new ArrayList<>();
        UnprocessedReferralsResponse unprocessedReferralResponse = new UnprocessedReferralsResponse();
        unprocessedReferralResponse.setAnalysisParameters(null);
        unprocessedReferralResponse.setComment("Komentar");
        expectedResponse.add(unprocessedReferralResponse);

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setPbo(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(referralRepository.findByLbpAndPboReferredToAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN)).thenReturn((unprocessedReferrals));

        List<UnprocessedReferralsResponse> actualResponse = referralService.unprocessedReferrals(lbp, token);
        actualResponse.get(0).setCreationDate(null);

        assertEquals(expectedResponse, actualResponse);
        verify(referralRepository, times(1)).findByLbpAndPboReferredToAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN);
    }

    private  void mockConnectionWithUserService_Doctors() {
        Mockito.mockStatic(HttpUtils.class);

        DoctorResponse doctorResponseMock = Mockito.mock(DoctorResponse.class);
        List<DoctorResponse> doctorResponsesMock = new ArrayList<>();
        doctorResponsesMock.add(doctorResponseMock);

        ResponseEntity<List<DoctorResponse>> responseBodyMock = Mockito.mock(ResponseEntity.class);

        when(responseBodyMock.getBody()).thenReturn(doctorResponsesMock);

        doReturn(HttpStatus.OK).when(responseBodyMock).getStatusCode();

        when(HttpUtils.getAllDoctors(any(String.class))).thenReturn(responseBodyMock);
    }

    private  void mockConnectionWithUserService_Departments() {
        DepartmentResponse departmentResponse = Mockito.mock(DepartmentResponse.class);
        List<DepartmentResponse> departmentResponseMock = new ArrayList<>();
        departmentResponseMock.add(departmentResponse);

        ResponseEntity<List<DepartmentResponse>> responseBodyMock = Mockito.mock(ResponseEntity.class);

        when(responseBodyMock.getBody()).thenReturn(departmentResponseMock);

        doReturn(HttpStatus.OK).when(responseBodyMock).getStatusCode();

        when(HttpUtils.findDepartmentName(any(String.class))).thenReturn(responseBodyMock);
    }

    private ReferralListResponse createReferralListResponse() {
        List<ReferralResponse> referralResponses = new ArrayList<>();
        ReferralResponse referral1 = new ReferralResponse();
        referral1.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        referral1.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        referralResponses.add(referral1);
        ReferralListResponse referralListResponse = new ReferralListResponse(referralResponses, 1L);
        return referralListResponse;
    }
}
