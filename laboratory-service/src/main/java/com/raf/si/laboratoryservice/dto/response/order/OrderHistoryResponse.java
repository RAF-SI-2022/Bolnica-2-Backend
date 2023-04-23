package com.raf.si.laboratoryservice.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryResponse {
    private List<OrderResponse> orderList;
    private Long count;
}
