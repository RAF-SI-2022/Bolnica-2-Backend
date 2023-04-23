package com.raf.si.laboratoryservice.dto.response.order;

import com.raf.si.laboratoryservice.model.AnalysisParameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnalysisResponse {
    private Long id;
    private String name;
    private String abbreviation;
}
