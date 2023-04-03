package com.raf.si.laboratoryservice.services;


import com.raf.si.laboratoryservice.dto.request.order.*;
import com.raf.si.laboratoryservice.dto.response.order.*;
import org.springframework.data.domain.Pageable;


public interface WorkOrderService {
    CreateOrderResponse createOrder(CreateOrderRequest orderRequest);
    OrderHistoryResponse orderHistory(OrderHistoryRequest historyRequest, Pageable pageable);
    SaveResultResponse saveResult(SaveResultRequest saveRequest);
    ResultResponse getResults(ResultRequest resultRequest);
    OrderHistoryResponse orderHistoryForLab(OrderHistoryForLabRequest request, Pageable pageable);
    VerifyResponse verify(VerifyRequest verifyRequest);
}
