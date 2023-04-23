package com.raf.si.laboratoryservice.services;


import com.raf.si.laboratoryservice.dto.request.order.*;
import com.raf.si.laboratoryservice.dto.response.order.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Pageable;


public interface WorkOrderService {
    OrderResponse createOrder(Long orderId);
    OrderHistoryResponse orderHistory(OrderHistoryRequest historyRequest, Pageable pageable);
    SaveResultResponse saveResult(SaveResultRequest saveRequest);
    ResultResponse getResults(Long orderId);
    OrderHistoryResponse orderHistoryForLab(OrderHistoryForLabRequest request, Pageable pageable);
    OrderResponse verify(Long orderId);
}
