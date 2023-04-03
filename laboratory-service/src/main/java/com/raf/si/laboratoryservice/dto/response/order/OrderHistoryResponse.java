package com.raf.si.laboratoryservice.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderHistoryResponse {
    private List<OrderResponse> orderList;
    private Long count;
}
