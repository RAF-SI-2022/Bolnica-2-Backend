package com.raf.si.laboratoryservice.mapper;

import com.raf.si.laboratoryservice.dto.response.order.*;
import com.raf.si.laboratoryservice.model.*;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@NoArgsConstructor
public class OrderMapper {

    public OrderResponse orderToOrderResponse(LabWorkOrder order){
        List<AnalysisResultResponse> analysisResultResponses = new ArrayList<>();
        List<AnalysisParameterResult> results = order.getAnalysisParameterResults();
        if(results != null) {
            results.forEach(result -> analysisResultResponses.add(analysisReportToAnalysisReportResponse(result)));
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCreationTime(order.getCreationTime());
        response.setLbp(order.getLbp());
        response.setStatus(order.getStatus());
        response.setLbzTechnician(order.getLbzTechnician());
        response.setLbzBiochemist(order.getLbzBiochemist());
        response.setAnalysisParameterResults(analysisResultResponses);
        response.setReferralId(order.getReferral().getId());

        return response;
    }

    public OrderResponse orderToOrderResponse(LabWorkOrder order, List<AnalysisParameterResult> results) {
        OrderResponse response = orderToOrderResponse(order);
        List<AnalysisResultResponse> analysisResultResponses = new ArrayList<>();
        if(results != null) {
            results.forEach(result -> analysisResultResponses.add(analysisReportToAnalysisReportResponse(result)));
        }
        response.setAnalysisParameterResults(analysisResultResponses);
        return response;
    }

    public OrderHistoryResponse orderPageToOrderHistoryResponse(Page<LabWorkOrder> orderPage){
        List<OrderResponse> orders = orderPage.getContent()
                .stream()
                .map(this::orderToOrderResponse)
                .collect(Collectors.toList());

        return new OrderHistoryResponse(orders, orderPage.getTotalElements());
    }

    public SaveResultResponse resultToSaveResultResponse(AnalysisParameterResult apr) {
        SaveResultResponse response = new SaveResultResponse();
        response.setId(apr.getId());
        response.setResult(apr.getResult());
        response.setDate(apr.getDateAndTime());
        response.setLbzBiochemist(apr.getLbzBiochemist());

        return response;
    }

    public OrderHistoryResponse orderPageToOrderHistoryForLabResponse(Page<LabWorkOrder> orderPage) {
        List<OrderResponse> orders = orderPage.getContent()
                .stream()
                .filter(this::isWorkOrderReferredToMyDepartment)
                .map(this::orderToOrderResponse)
                .collect(Collectors.toList());

        return new OrderHistoryResponse(orders, (long) orders.size());
    }

    private boolean isWorkOrderReferredToMyDepartment(LabWorkOrder order){
        UUID pbo = TokenPayloadUtil.getTokenPayload().getPbo();
        return order.getReferral().getPboReferredTo().equals(pbo);
    }

    public ResultResponse orderToResultResponse(LabWorkOrder order){
        ResultResponse response = new ResultResponse();
        response.setOrderId(order.getId());
        response.setOrderStatus(order.getStatus());
        response.setOrderCreationTime(order.getCreationTime());
        response.setReferralId(order.getReferral().getId());
        response.setLbp(order.getLbp());
        response.setLbzTechnician(order.getLbzTechnician());
        response.setResults(new ArrayList<>());
        for(AnalysisParameterResult apr : order.getAnalysisParameterResults()){
            response.getResults().add(analysisReportToAnalysisReportResponse(apr));
        }

        return response;
    }

    private AnalysisResultResponse analysisReportToAnalysisReportResponse(AnalysisParameterResult apr){
        AnalysisResultResponse response = new AnalysisResultResponse();
        response.setId(apr.getId());
        response.setResult(apr.getResult());
        response.setLbzBiochemist(apr.getLbzBiochemist());
        response.setDateAndTime(apr.getDateAndTime());
        response.setAnalysis(labAnalysisToAnalysisResponse(apr.getAnalysisParameter().getAnalysis()));
        response.setParameter(analysisParameterToAnalysisParameterResponse(apr.getAnalysisParameter()));

        return response;
    }

    private AnalysisParameterResponse analysisParameterToAnalysisParameterResponse(AnalysisParameter ap){
        Parameter parameter = ap.getParameter();
        AnalysisParameterResponse response = new AnalysisParameterResponse();
        response.setId(parameter.getId());
        response.setName(parameter.getName());
        response.setType(parameter.getType().getNotation());
        response.setMeasureUnit(parameter.getMeasureUnit());
        response.setUpperBound(parameter.getUpperBound());
        response.setLowerBound(parameter.getLowerBound());

        return response;
    }

    private AnalysisResponse labAnalysisToAnalysisResponse(LabAnalysis la){
        AnalysisResponse response = new AnalysisResponse();
        response.setId(la.getId());
        response.setName(la.getName());
        response.setAbbreviation(la.getAbbreviation());

        return response;
    }
}
