package com.raf.si.laboratoryservice.dto.request.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveResultRequest {
    private Long orderId;
    private Long parameterId;
    private String result;
}
