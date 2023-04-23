package com.raf.si.laboratoryservice.dto.response.order;

import com.raf.si.laboratoryservice.model.enums.parameter.ParameterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
public class AnalysisParameterResponse {
    private Long id;
    private String name;
    private String type;
    private String measureUnit;
    private Double lowerBound;
    private Double upperBound;
}
