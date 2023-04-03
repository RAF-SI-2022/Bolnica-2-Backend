package com.raf.si.laboratoryservice.services.impl;

import antlr.Token;
import com.raf.si.laboratoryservice.dto.request.order.*;
import com.raf.si.laboratoryservice.dto.response.order.*;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.OrderMapper;
import com.raf.si.laboratoryservice.model.*;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.model.enums.user.Profession;
import com.raf.si.laboratoryservice.model.enums.user.Title;
import com.raf.si.laboratoryservice.repository.*;
import com.raf.si.laboratoryservice.services.WorkOrderService;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class WorkOrderServiceImplementation implements WorkOrderService {

    ReferralRepository referralRepository;
    LabAnalysisRepository labAnalysisRepository;
    LabWorkOrderRepository labWorkOrderRepository;
    AnalysisParameterResultRepository analysisParameterResultRepository;
    OrderMapper orderMapper;
    AnalysisParameterRepository analysisParameterRepository;

    public WorkOrderServiceImplementation(ReferralRepository referralRepository, LabAnalysisRepository labAnalysisRepository, LabWorkOrderRepository labWorkOrderRepository, AnalysisParameterResultRepository analysisParameterResultRepository, OrderMapper orderMapper, AnalysisParameterRepository analysisParameterRepository) {
        this.referralRepository = referralRepository;
        this.labAnalysisRepository = labAnalysisRepository;
        this.labWorkOrderRepository = labWorkOrderRepository;
        this.analysisParameterResultRepository = analysisParameterResultRepository;
        this.orderMapper = orderMapper;
        this.analysisParameterRepository = analysisParameterRepository;
    }

    @Override
    public OrderResponse createOrder(Long orderId) {
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        UUID lbz = tokenPayload.getLbz();

        Optional<Referral> referral = referralRepository.findById(orderId);
        if(referral.isEmpty()){
            String errMessage = String.format("Uput sa id-om '%s' ne postoji", orderId);
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        UUID lbp = referral.get().getLbp();
        String[] requiredAnalysis = referral.get().getRequiredAnalysis().split(",");

        LabWorkOrder newOrder = new LabWorkOrder();
        newOrder.setLbp(lbp);
        newOrder.setLbzTechnician(lbz);
        newOrder.setCreationTime(new Date(System.currentTimeMillis()));

        for(String name : requiredAnalysis){
            Optional<LabAnalysis> analysisOptional = labAnalysisRepository.findByName(name);
            if(analysisOptional.isEmpty()){
                String errMessage = String.format("Nepoznata analiza %s zahtevana", name);
                log.info(errMessage);
                throw new NotFoundException(errMessage);
            }
            LabAnalysis analysis = analysisOptional.get();
            for (AnalysisParameter ap : analysis.getAnalysisParameters()){
                AnalysisParameterResult analysisParameterResult = new AnalysisParameterResult();
                analysisParameterResult.setLabWorkOrder(newOrder);
                analysisParameterResult.setAnalysisParameter(ap);
                analysisParameterResultRepository.save(analysisParameterResult);
            }
        }

        labWorkOrderRepository.save(newOrder);
        return orderMapper.orderToOrderResponse(newOrder);
    }

    @Override
    public OrderHistoryResponse orderHistory(OrderHistoryRequest historyRequest, Pageable pageable) {
        Page<LabWorkOrder> orders = labWorkOrderRepository.findByLbpAndCreationTimeBetweenAndStatusIsNot(
                historyRequest.getLbp(), historyRequest.getStartDate(), historyRequest.getEndDate(),
                OrderStatus.NEOBRADJEN, pageable
        );
        return orderMapper.orderPageToOrderHistoryResponse(orders);
    }

    @Override
    public SaveResultResponse saveResult(SaveResultRequest saveRequest) {
        LabWorkOrder order = findOrder(saveRequest.getOrderId());
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

        return orderMapper.resultToSaveResultResponse(result);
    }

    @Override
    public ResultResponse getResults(Long orderId) {
        List<String> permissions = TokenPayloadUtil.getTokenPayload().getPermissions();
        LabWorkOrder order = findOrder(orderId);
        if (!permissions.contains("ROLE_MED_BIOHEM") && !permissions.contains("ROLE_SPEC_MED_BIOHEM")) {
            if (!order.getStatus().equals(OrderStatus.OBRADJEN)) {
                String errMessage = String.format("Radni nalog sa id-om '%s' nije obradjen", orderId);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }
        return orderMapper.orderToResultResponse(order);
    }

    @Override
    public OrderHistoryResponse orderHistoryForLab(OrderHistoryForLabRequest request, Pageable pageable) {
        Page<LabWorkOrder> orders = labWorkOrderRepository.findByLbpAndCreationTimeBetweenAndStatus(
                request.getLbp(),request.getStartDate(),request.getEndDate(), OrderStatus.valueOfNotation(request.getOrderStatus()),
                pageable
        );

        return orderMapper.orderPageToOrderHistoryForLabResponse(orders);
    }

    @Override
    public OrderResponse verify(Long orderId) {
        LabWorkOrder order = findOrder(orderId);

        for(AnalysisParameterResult apr : order.getAnalysisParameterResults()){
            if(apr.getResult() == null){
                String errMessage = "Nisu unete svi rezultati analize parametara.";
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }

        order.setStatus(OrderStatus.OBRADJEN);
        order.setLbzBiochemist(TokenPayloadUtil.getTokenPayload().getLbz());

        Referral referral = order.getReferral();
        referral.setStatus(ReferralStatus.REALIZOVAN);

        labWorkOrderRepository.save(order);
        referralRepository.save(referral);

        return orderMapper.orderToOrderResponse(order);
    }

    private LabWorkOrder findOrder(Long orderId){
        Optional<LabWorkOrder> order = labWorkOrderRepository.findById(orderId);
        if(order.isEmpty()){
            String errMessage = String.format("Radni nalog sa id-om '%s' ne postoji", orderId);
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        return order.get();
    }
}
