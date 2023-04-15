package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.ReferralListResponse;
import com.raf.si.laboratoryservice.dto.response.ReferralResponse;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.ReferralMapper;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.service.ReferralService;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ReferralServiceImpl implements ReferralService {

    private final ReferralRepository referralRepository;
    private final LabWorkOrderRepository labWorkOrderRepository;
    private final ReferralMapper referralMapper;

    public ReferralServiceImpl(ReferralRepository referralRepository, LabWorkOrderRepository labWorkOrderRepository, ReferralMapper referralMapper) {
        this.referralRepository = referralRepository;
        this.labWorkOrderRepository = labWorkOrderRepository;
        this.referralMapper = referralMapper;
    }

    @Override
    public ReferralResponse createReferral(CreateReferralRequest createReferralRequest) {
        Referral referral = referralRepository.save(referralMapper.requestToModel(createReferralRequest));
        return referralMapper.modelToResponse(referral);
    }

    @Override
    public ReferralListResponse referralHistory(UUID lbp, Date dateFrom, Date dateTo, Pageable pageable) {
        Date endDate = DateUtils.addDays(dateTo, 1);
        Page<Referral> referralPage = referralRepository.findByLbpAndCreationTimeBetweenAndDeletedFalse(lbp, dateFrom, endDate, pageable);
        return referralMapper.referralPageToReferralListResponse(referralPage);
    }
    @Override
    public ReferralResponse getReferral(Long id) {
        Referral referral = referralRepository.findById(id).orElseThrow(() -> {
            log.error("Ne postoji uput sa id-ijem '{}'", id);
            throw new NotFoundException("Uput sa datim id-ijem ne postoji");
        });
        return referralMapper.modelToResponse(referral);
    }

    public ReferralResponse deleteReferral(Long id) {
        Referral referral = referralRepository.findById(id).orElseThrow(() -> {
            log.error("Ne postoji uput sa id-ijem '{}'", id);
            throw new NotFoundException("Uput sa datim id-ijem ne postoji");
        });

        UUID lbzFromToken = TokenPayloadUtil.getTokenPayload().getLbz();
        boolean lbzMatch = referral.getLbz().equals(lbzFromToken);

        if (!lbzMatch) {
            log.error("Lbz uputa i lbz iz tokena se ne poklapaju");
            throw new NotFoundException("Lbz uputa i lbz iz tokena se ne poklapaju, uput nije obrisan!");
        }

        if (labWorkOrderRepository.findByReferral(referral) != null) {
            log.error("Radni nalog sa id-ijem ('{}') uputa postoji ", id);
            throw new NotFoundException("Radni nalog sa id-ijem uputa postoji, uput nije obrisan!");
        }

        referral.setDeleted(true);
        referral = referralRepository.save(referral);
        log.info("Uput sa id-ijem '{}' je uspesno obrisan", id);
        return referralMapper.modelToResponse(referral);
    }

    @Override
    public ReferralListResponse unprocessedReferrals(UUID lbp) {
        UUID pboFromToken = TokenPayloadUtil.getTokenPayload().getPbo();
        List<Referral> unprocessedReferralsByThreeParams =
                referralRepository.findByLbpAndPboAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN).orElseThrow(() -> {
                    log.error("Ne postoji trazeni uput");
                    throw new NotFoundException("Uput sa zadatim parametrima nije pronadjen");
                });

        List<Referral> unprocessedReferrals = unprocessedReferralsByThreeParams.stream()
                .filter(referral -> referral.getLabWorkOrder() == null)
                .collect(Collectors.toList());

        return referralMapper.referralListToListResponse(unprocessedReferrals);
    }
}
