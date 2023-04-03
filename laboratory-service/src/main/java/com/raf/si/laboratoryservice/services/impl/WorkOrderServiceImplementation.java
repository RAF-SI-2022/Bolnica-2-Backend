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
import com.raf.si.laboratoryservice.repository.AnalysisParameterResultRepository;
import com.raf.si.laboratoryservice.repository.LabAnalysisRepository;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.services.WorkOrderService;
import com.raf.si.laboratoryservice.utils.TokenPayload;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
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

    public WorkOrderServiceImplementation(ReferralRepository referralRepository, LabAnalysisRepository labAnalysisRepository, LabWorkOrderRepository labWorkOrderRepository, AnalysisParameterResultRepository analysisParameterResultRepository, ReferralRepository referralRepository1, OrderMapper orderMapper) {
        this.labAnalysisRepository = labAnalysisRepository;
        this.labWorkOrderRepository = labWorkOrderRepository;
        this.analysisParameterResultRepository = analysisParameterResultRepository;
        this.referralRepository = referralRepository1;
        this.orderMapper = orderMapper;
    }

    //TODO prebaciti na mapper
    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest orderRequest) {
        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        UUID lbz = tokenPayload.getLbz();

        Optional<Referral> referral = referralRepository.findById(orderRequest.getId());
        if(referral.isEmpty()){
            String errMessage = String.format("Uput sa id-om '%s' ne postoji", orderRequest.getId());
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
        return new CreateOrderResponse("Uspešno kreiran radni nalog sa id-em: " + newOrder.getId());
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
        orderMapper.saveRequestToAnalysisResult(saveRequest);

        return new SaveResultResponse("Uspešno unet rezultat analize.");
    }

    @Override
    public ResultResponse getResults(ResultRequest resultRequest) {
        List<String> permisions = TokenPayloadUtil.getTokenPayload().getPermissions();
        Optional<LabWorkOrder> order = labWorkOrderRepository.findById(resultRequest.getOrderId());
        if(permisions.contains("ROLE_MED_BIOHEM") || permisions.contains("ROLE_SPEC_MED_BIOHEM")){
            if(order.isEmpty()){
                String errMessage = String.format("Radni nalog sa id-om '%s' ne postoji", resultRequest.getOrderId());
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }else{
            if(order.isEmpty()){
                String errMessage = String.format("Radni nalog sa id-om '%s' ne postoji", resultRequest.getOrderId());
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
            if(!order.get().getStatus().equals(OrderStatus.OBRADJEN)){
                String errMessage = String.format("Radni nalog sa id-om '%s' nije obradjen", resultRequest.getOrderId());
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
        }
        return orderMapper.orderToResultResponse(order.get());
    }

    @Override
    public OrderHistoryResponse orderHistoryForLab(OrderHistoryForLabRequest request, Pageable pageable) {
        Page<LabWorkOrder> orders = labWorkOrderRepository.findByLbpAndCreationTimeBetweenAndStatus(
                request.getLbp(),request.getStartDate(),request.getEndDate(),request.getOrderStatus(),
                pageable
        );

        return orderMapper.orderPageToOrderHistoryForLabResponse(orders);
    }

    @Override
    public VerifyResponse verify(VerifyRequest verifyRequest) {
        Optional<LabWorkOrder> orderOptional = labWorkOrderRepository.findById(verifyRequest.getOrderId());

        if(orderOptional.isEmpty()){
            String errMessage = String.format("Radni nalog sa id-om '%s' ne postoji", verifyRequest.getOrderId());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        LabWorkOrder order = orderOptional.get();
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

        return new VerifyResponse("Radni nalog je uspešno realizovan.");
    }
}
