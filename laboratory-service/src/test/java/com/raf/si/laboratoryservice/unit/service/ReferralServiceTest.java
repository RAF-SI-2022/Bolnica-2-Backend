package com.raf.si.laboratoryservice.unit.service;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.*;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.exception.InternalServerErrorException;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.ReferralMapper;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.service.impl.ReferralServiceImpl;
import com.raf.si.laboratoryservice.utils.HttpUtils;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
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
    void testDeleteReferral_Success() {
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
    void testDeleteReferral_ReferralNotFound() {
        Long referralId = 1L;
        when(referralRepository.findById(referralId)).thenReturn(Optional.empty());

        try {
            referralService.deleteReferral(referralId);
            fail("Expected NotFoundException not thrown");
        } catch (NotFoundException e) {
            assertEquals("Uput sa datim id-ijem ne postoji", e.getMessage());
        }
    }

    @Test
    void testDeleteReferral_LbzMismatch() {
        Long referralId = 1L;
        Referral referral = new Referral();
        referral.setLbz(UUID.randomUUID());

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(tokenPayload);

        when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));

        try {
            referralService.deleteReferral(referralId);
            fail("Expected NotFoundException not thrown");
        } catch (NotFoundException e) {
            assertEquals("Lbz uputa i lbz iz tokena se ne poklapaju, uput nije obrisan!", e.getMessage());
        }
    }

    @Test
    void testDeleteReferral_WorkOrderExists() {
        Long referralId = 1L;
        Referral referral = new Referral();
        referral.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(tokenPayload);

        when(referralRepository.findById(referralId)).thenReturn(Optional.of(referral));
        when(labWorkOrderRepository.findByReferral(referral)).thenReturn(new LabWorkOrder());

        try {
            referralService.deleteReferral(referralId);
            fail("Expected NotFoundException not thrown");
        } catch (NotFoundException e) {
            assertEquals("Radni nalog sa id-ijem uputa postoji, uput nije obrisan!", e.getMessage());
        }
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
    void testUnprocessedReferrals() {
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
        unprocessedReferralResponse.setRequiredAnalysis(null);
        unprocessedReferralResponse.setComment("Komentar");
        expectedResponse.add(unprocessedReferralResponse);

        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setPbo(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(referralRepository.findByLbpAndPboReferredFromAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN)).thenReturn((unprocessedReferrals));

        List<UnprocessedReferralsResponse> actualResponse = referralService.unprocessedReferrals(lbp, null, token);
        actualResponse.get(0).setCreationDate(null);

        assertEquals(expectedResponse, actualResponse);
        verify(referralRepository, times(1)).findByLbpAndPboReferredFromAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN);
    }

    @Test
    public void testRequestToModel() {
        CreateReferralRequest createReferralRequest = new CreateReferralRequest();
        createReferralRequest.setPboReferredFrom(UUID.randomUUID());
        createReferralRequest.setPboReferredTo(UUID.randomUUID());
        createReferralRequest.setLbz(UUID.randomUUID());
        createReferralRequest.setLbp(UUID.randomUUID());
        Date now = new Date();
        createReferralRequest.setCreationTime(new Date());
        createReferralRequest.setReferralReason("Test referral reason");
        createReferralRequest.setReferralDiagnosis("Test referral diagnosis");
        createReferralRequest.setComment("Test referral comment");
        createReferralRequest.setRequiredAnalysis("Test required analysis");
        createReferralRequest.setType("Laboratorija");
        ReferralType expectedReferralType = ReferralType.LABORATORIJA;

        ReferralMapper rm = new ReferralMapper();
        Referral referral = rm.requestToModel(createReferralRequest);

        assertEquals(createReferralRequest.getPboReferredFrom(), referral.getPboReferredFrom());
        assertEquals(createReferralRequest.getPboReferredTo(), referral.getPboReferredTo());
        assertEquals(createReferralRequest.getLbz(), referral.getLbz());
        assertEquals(createReferralRequest.getLbp(), referral.getLbp());
        assertEquals(now, referral.getCreationTime());
        assertEquals(createReferralRequest.getReferralReason(), referral.getReferralReason());
        assertEquals(createReferralRequest.getReferralDiagnosis(), referral.getReferralDiagnosis());
        assertEquals(createReferralRequest.getComment(), referral.getComment());
        assertEquals(createReferralRequest.getRequiredAnalysis(), referral.getRequiredAnalysis());
        assertEquals(false, referral.getDeleted());
        assertEquals(expectedReferralType, referral.getType());
    }

    @Test
    public void testModelToResponse() {
        Referral referral = new Referral();
        referral.setId(1L);
        referral.setType(ReferralType.LABORATORIJA);
        referral.setPboReferredFrom(UUID.randomUUID());
        referral.setPboReferredTo(UUID.randomUUID());
        referral.setLbz(UUID.randomUUID());
        referral.setLbp(UUID.randomUUID());
        referral.setCreationTime(new Date());
        referral.setReferralDiagnosis("diagnosis");
        referral.setReferralReason("reason");
        referral.setRequiredAnalysis("analysis");
        referral.setComment("comment");
        referral.setStatus(ReferralStatus.NEREALIZOVAN);
        referral.setDeleted(false);

        ReferralMapper rm = new ReferralMapper();
        ReferralResponse referralResponse = rm.modelToResponse(referral);

        assertEquals(referral.getId(), referralResponse.getId());
        assertEquals(referral.getType(), referralResponse.getType());
        assertEquals(referral.getPboReferredFrom(), referralResponse.getPboReferredFrom());
        assertEquals(referral.getPboReferredTo(), referralResponse.getPboReferredTo());
        assertEquals(referral.getLbz(), referralResponse.getLbz());
        assertEquals(referral.getLbp(), referralResponse.getLbp());
        assertEquals(referral.getCreationTime(), referralResponse.getCreationTime());
        assertEquals(referral.getReferralDiagnosis(), referralResponse.getReferralDiagnosis());
        assertEquals(referral.getReferralReason(), referralResponse.getReferralReason());
        assertEquals(referral.getRequiredAnalysis(), referralResponse.getRequiredAnalysis());
        assertEquals(referral.getComment(), referralResponse.getComment());
        assertEquals(referral.getStatus(), referralResponse.getStatus());
        assertEquals(referral.getDeleted(), referralResponse.getDeleted());
    }


    @Test
    public void testGettersAndSetters() {
        // Create a ReferralResponse object with test data
        ReferralResponse referral = new ReferralResponse();
        referral.setId(1L);
        referral.setType(ReferralType.LABORATORIJA);
        referral.setLbz(UUID.randomUUID());
        referral.setPboReferredFrom(UUID.randomUUID());
        referral.setPboReferredTo(UUID.randomUUID());
        referral.setLbp(UUID.randomUUID());
        referral.setCreationTime(new Date());
        referral.setStatus(ReferralStatus.NEREALIZOVAN);
        referral.setRequiredAnalysis("Blood");
        referral.setComment("Some comment");
        referral.setReferralDiagnosis("Diagnosis");
        referral.setReferralReason("Reason");
        referral.setDeleted(false);

        // Verify that the getters return the same values as the setters
        assertEquals(Long.valueOf(1L), referral.getId());
        assertEquals(ReferralType.LABORATORIJA, referral.getType());
        assertEquals(referral.getLbz(), referral.getLbz());
        assertEquals(referral.getPboReferredFrom(), referral.getPboReferredFrom());
        assertEquals(referral.getPboReferredTo(), referral.getPboReferredTo());
        assertEquals(referral.getLbp(), referral.getLbp());
        assertEquals(referral.getCreationTime(), referral.getCreationTime());
        assertEquals(ReferralStatus.NEREALIZOVAN, referral.getStatus());
        assertEquals("Blood", referral.getRequiredAnalysis());
        assertEquals("Some comment", referral.getComment());
        assertEquals("Diagnosis", referral.getReferralDiagnosis());
        assertEquals("Reason", referral.getReferralReason());
        assertFalse(referral.getDeleted());
    }

    private void mockConnectionWithUserService_Doctors() {
        Mockito.mockStatic(HttpUtils.class);

        DoctorResponse doctorResponseMock = Mockito.mock(DoctorResponse.class);
        List<DoctorResponse> doctorResponseList = new ArrayList<>();
        doctorResponseList.add(doctorResponseMock);

        ResponseEntity<DoctorResponse> responseBodyMock = Mockito.mock(ResponseEntity.class);
        doReturn(HttpStatus.OK).when(responseBodyMock).getStatusCode();

        when(HttpUtils.getAllDoctors(any(String.class))).thenReturn(doctorResponseList);
    }

    private void mockConnectionWithUserService_Departments() {
        DepartmentResponse departmentResponseMock = Mockito.mock(DepartmentResponse.class);
        List<DepartmentResponse> departmentResponsesList = new ArrayList<>();
        departmentResponsesList.add(departmentResponseMock);

        ResponseEntity<DepartmentResponse> responseBodyMock = Mockito.mock(ResponseEntity.class);
        when(responseBodyMock.getBody()).thenReturn(departmentResponseMock);

        doReturn(HttpStatus.OK).when(responseBodyMock).getStatusCode();

        when(HttpUtils.findDepartmentName(any(String.class))).thenReturn(departmentResponsesList);
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
