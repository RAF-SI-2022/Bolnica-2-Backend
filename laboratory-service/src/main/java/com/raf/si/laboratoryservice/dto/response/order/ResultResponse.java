package com.raf.si.laboratoryservice.dto.response.order;

import com.raf.si.laboratoryservice.model.AnalysisParameter;
import com.raf.si.laboratoryservice.model.AnalysisParameterResult;
import com.raf.si.laboratoryservice.model.LabAnalysis;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ResultResponse {
    LabWorkOrder order;
    List<AnalysisParameterResult> resultList;
    List<AnalysisParameter> parameterList;
    List<LabAnalysis> analysisList;
}
