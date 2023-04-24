package com.raf.si.laboratoryservice.unit.service;

import com.raf.si.laboratoryservice.dto.request.order.OrderHistoryForLabRequest;
import com.raf.si.laboratoryservice.dto.request.order.OrderHistoryRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.dto.response.order.OrderHistoryResponse;
import com.raf.si.laboratoryservice.dto.response.order.OrderResponse;
import com.raf.si.laboratoryservice.mapper.OrderMapper;
import com.raf.si.laboratoryservice.model.AnalysisParameterResult;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.repository.*;
import com.raf.si.laboratoryservice.service.WorkOrderService;
import com.raf.si.laboratoryservice.service.impl.WorkOrderServiceImplementation;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Ref;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
public class WorkOrderServiceTest {
    private ReferralRepository referralRepository;
    private LabAnalysisRepository labAnalysisRepository;
    private LabWorkOrderRepository labWorkOrderRepository;
    private AnalysisParameterResultRepository analysisParameterResultRepository;
    private OrderMapper orderMapper;
    private AnalysisParameterRepository analysisParameterRepository;
    private WorkOrderService workOrderService;
    private Authentication authentication;
    @BeforeEach
    void setup(){
        referralRepository = mock(ReferralRepository.class);
        labAnalysisRepository = mock(LabAnalysisRepository.class);
        labWorkOrderRepository = mock(LabWorkOrderRepository.class);
        analysisParameterResultRepository = mock(AnalysisParameterResultRepository.class);
        orderMapper = mock(OrderMapper.class);
        analysisParameterRepository = mock(AnalysisParameterRepository.class);
        authentication = mock(Authentication.class);
        workOrderService = new WorkOrderServiceImplementation(referralRepository, labAnalysisRepository, labWorkOrderRepository,
                analysisParameterResultRepository, orderMapper, analysisParameterRepository);
    }

    @Test
    public void testOrderHistory(){
        UUID lbp = UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8");
        Date dateFrom = new Date();
        Date dateTo = new Date();
        Pageable pageable = PageRequest.of(0, 10);

        OrderHistoryRequest orderHistoryRequest = new OrderHistoryRequest(dateFrom,
                dateTo, lbp);


        LabWorkOrder workOrder = new LabWorkOrder();
        workOrder.setLbp(lbp);
        Page<LabWorkOrder> workOrderPage = new PageImpl<>(Collections.singletonList(workOrder));

        OrderHistoryResponse orderHistoryResponse = createOrderHistoryResponse();

        when(labWorkOrderRepository.findByLbpAndCreationTimeBetweenAndStatusIsNot(eq(lbp), eq(dateFrom), any(Date.class),
                eq(OrderStatus.NEOBRADJEN),eq(pageable))).thenReturn(workOrderPage);
        when(orderMapper.orderPageToOrderHistoryResponse(workOrderPage)).thenReturn(orderHistoryResponse);

        // Act
        OrderHistoryResponse result = workOrderService.orderHistory(orderHistoryRequest, pageable);

        assertNotNull(result);
        assertEquals(orderHistoryResponse, result);
    }

    @Test
    public void testOrderHistoryForLab(){
        UUID lbp = UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8");
        Date dateFrom = new Date();
        Date dateTo = new Date();
        Pageable pageable = PageRequest.of(0, 10);

        OrderHistoryForLabRequest orderHistoryRequest = new OrderHistoryForLabRequest(dateFrom,
                dateTo, lbp, "Neobrađen");


        LabWorkOrder workOrder = new LabWorkOrder();
        workOrder.setLbp(lbp);
        Page<LabWorkOrder> workOrderPage = new PageImpl<>(Collections.singletonList(workOrder));

        OrderHistoryResponse orderHistoryResponse = createOrderHistoryResponse();

        when(labWorkOrderRepository.findByLbpAndCreationTimeBetweenAndStatus(eq(lbp), eq(dateFrom), any(Date.class),
                any(OrderStatus.class), eq(pageable))).thenReturn(workOrderPage);
        when(orderMapper.orderPageToOrderHistoryForLabResponse(workOrderPage)).thenReturn(orderHistoryResponse);

        // Act
        OrderHistoryResponse result = workOrderService.orderHistoryForLab(orderHistoryRequest, pageable);

        assertNotNull(result);
        assertEquals(orderHistoryResponse, result);
    }

    @Test
    public void testForVerify(){
        Long orderId = 1234L;
        LabWorkOrder order = new LabWorkOrder();
        order.setId(orderId);
        AnalysisParameterResult apr1 = new AnalysisParameterResult();
        apr1.setResult("result 1");
        AnalysisParameterResult apr2 = new AnalysisParameterResult();
        apr2.setResult("result 2");
        List<AnalysisParameterResult> aprs = new ArrayList<>();
        aprs.add(apr1);
        aprs.add(apr2);
        order.setAnalysisParameterResults(aprs);
        Referral referral = new Referral();
        order.setReferral(referral);



        TokenPayload tokenPayload = new TokenPayload();
        tokenPayload.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LabWorkOrder newOrder = new LabWorkOrder();
        order.setId(orderId);
        order.setAnalysisParameterResults(aprs);
        order.setLbzBiochemist(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        order.setStatus(OrderStatus.OBRADJEN);
        Referral newReferral = new Referral();
        newReferral.setStatus(ReferralStatus.REALIZOVAN);
        newOrder.setReferral(newReferral);

        OrderResponse orderResponse = new OrderResponse();

        when(authentication.getPrincipal()).thenReturn(tokenPayload);
        when(labWorkOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(labWorkOrderRepository.save(order)).thenReturn(newOrder);
        when(orderMapper.orderToOrderResponse(newOrder)).thenReturn(orderResponse);

        //Act
        OrderResponse result = workOrderService.verify(1234L);

        verify(labWorkOrderRepository).findById(orderId);
        verify(labWorkOrderRepository).save(order);
        verify(orderMapper).orderToOrderResponse(newOrder);
        assertEquals(result, orderResponse);
    }

    private OrderHistoryResponse createOrderHistoryResponse(){
        List<OrderResponse> orderResponses = new ArrayList<>();
        OrderResponse order = new OrderResponse();
        order.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        order.setLbzTechnician(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        orderResponses.add(order);
        return new OrderHistoryResponse(orderResponses, 1L);
    }
}

