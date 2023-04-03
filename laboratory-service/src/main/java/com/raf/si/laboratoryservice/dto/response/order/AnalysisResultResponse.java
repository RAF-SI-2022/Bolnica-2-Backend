package com.raf.si.laboratoryservice.dto.response.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AnalysisResultResponse {
    private Long id;
    private String result;
    private Date dateAndTime;
    private UUID lbzBiochemist;
    private AnalysisResponse analysis;
    private AnalysisParameterResponse parameter;
}
