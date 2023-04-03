package com.raf.si.laboratoryservice.mapper;

import com.raf.si.laboratoryservice.dto.request.order.SaveResultRequest;
import com.raf.si.laboratoryservice.dto.response.order.OrderHistoryResponse;
import com.raf.si.laboratoryservice.dto.response.order.OrderResponse;
import com.raf.si.laboratoryservice.dto.response.order.ResultResponse;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.model.AnalysisParameter;
import com.raf.si.laboratoryservice.model.AnalysisParameterResult;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.repository.AnalysisParameterRepository;
import com.raf.si.laboratoryservice.repository.AnalysisParameterResultRepository;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderMapper {
    LabWorkOrderRepository labWorkOrderRepository;
    AnalysisParameterResultRepository analysisParameterResultRepository;
    AnalysisParameterRepository analysisParameterRepository;

    public OrderMapper(LabWorkOrderRepository labWorkOrderRepository, AnalysisParameterResultRepository analysisParameterResultRepository) {
        this.labWorkOrderRepository = labWorkOrderRepository;
        this.analysisParameterResultRepository = analysisParameterResultRepository;
    }

    public OrderResponse orderToOrderResponse(LabWorkOrder order){
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCreationTime(order.getCreationTime());
        response.setLbp(order.getLbp());
        response.setStatus(order.getStatus());
        response.setLbzTechnician(order.getLbzTechnician());
        response.setLbzBiochemist(order.getLbzBiochemist());
        response.setAnalysisParameterResults(order.getAnalysisParameterResults());
        response.setReferralId(order.getReferral().getId());

        return response;
    }

    public OrderHistoryResponse orderPageToOrderHistoryResponse(Page<LabWorkOrder> orderPage){
        List<OrderResponse> orders = orderPage.getContent()
                .stream()
                .map(this::orderToOrderResponse)
                .collect(Collectors.toList());

        return new OrderHistoryResponse(orders, orderPage.getTotalElements());
    }

    public void saveRequestToAnalysisResult(SaveResultRequest saveRequest) {
        Optional<LabWorkOrder> orderOptional = labWorkOrderRepository.findById(saveRequest.getOrderId());
        if(orderOptional.isEmpty()){
            String errMessage = String.format("Ne postoji radni nalog sa datim id-em: %s",
                    saveRequest.getOrderId());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        LabWorkOrder order = orderOptional.get();
        if (order.getStatus().equals(OrderStatus.NEOBRADJEN)){
            order.setStatus(OrderStatus.U_OBRADI);
            labWorkOrderRepository.save(order);
        }

        Optional<AnalysisParameter> analysisParameterOptional =  analysisParameterRepository.findById(
                saveRequest.getParameterId());

        if(analysisParameterOptional.isEmpty()){
            String errMessage = String.format("Ne postoji parametar sa datim id-em: %s",
                    saveRequest.getOrderId());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        AnalysisParameter analysisParameter = analysisParameterOptional.get();

        Optional<AnalysisParameterResult> resultOptional = analysisParameterResultRepository
                .findAnalysisParameterResultByLabWorkOrderAndAnalysisParameter(
                        order, analysisParameter
                );

        if(resultOptional.isEmpty()){
            String errMessage = "Ne postoji rezultat sa date parametre.";
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        AnalysisParameterResult result = resultOptional.get();
        result.setResult(saveRequest.getResult());
        result.setDateAndTime(new Date(System.currentTimeMillis()));
        result.setLbzBiochemist(TokenPayloadUtil.getTokenPayload().getLbz());

        analysisParameterResultRepository.save(result);
    }

    public OrderHistoryResponse orderPageToOrderHistoryForLabResponse(Page<LabWorkOrder> orderPage) {
        List<OrderResponse> orders = orderPage.getContent()
                .stream()
                .filter(this::shouldRemove)
                .map(this::orderToOrderResponse)
                .collect(Collectors.toList());

        return new OrderHistoryResponse(orders, orderPage.getTotalElements());
    }

    private boolean shouldRemove(LabWorkOrder order){
        UUID pbo = TokenPayloadUtil.getTokenPayload().getPbo();
        return !order.getReferral().getPboReferredTo().equals(pbo);
    }

    public ResultResponse orderToResultResponse(LabWorkOrder order){
        ResultResponse response = new ResultResponse();
        response.setOrder(order);
        response.setParameterList(new ArrayList<>());
        response.setAnalysisList(new ArrayList<>());
        List<AnalysisParameterResult> results = order.getAnalysisParameterResults();
        for(AnalysisParameterResult apr : results){
            response.getAnalysisList().add(apr.getAnalysisParameter().getAnalysis());
            response.getParameterList().add(apr.getAnalysisParameter());
        }
        response.setResultList(results);

        return response;
    }
}
