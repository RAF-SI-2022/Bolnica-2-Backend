package com.raf.si.laboratoryservice.dto.response.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SaveResultResponse {
    private Long id;
    private String result;
    private Date date;
    private UUID lbzBiochemist;
}
