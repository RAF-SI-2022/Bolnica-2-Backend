package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.VisitRequest;
import com.raf.si.patientservice.dto.response.VisitListResponse;
import com.raf.si.patientservice.dto.response.VisitResponse;
import com.raf.si.patientservice.mapper.VisitMapper;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.Visit;
import com.raf.si.patientservice.repository.VisitRepository;
import com.raf.si.patientservice.service.HospitalizationService;
import com.raf.si.patientservice.service.VisitService;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final VisitMapper visitMapper;
    private final HospitalizationService hospitalizationService;

    public VisitServiceImpl(VisitRepository visitRepository, VisitMapper visitMapper,
                            HospitalizationService hospitalizationService) {
        this.visitRepository = visitRepository;
        this.visitMapper = visitMapper;
        this.hospitalizationService = hospitalizationService;
    }

    @Override
    public VisitResponse createVisit(UUID lbp, VisitRequest visitRequest) {
        Patient patient = hospitalizationService.getHospitalisedPatientByLbp(lbp);
        Visit visit = visitMapper.visitRequestToModel(TokenPayloadUtil.getTokenPayload().getLbz(), patient, visitRequest);
        log.info("Saving new visit {}", visit);
        return visitMapper.modelToVisitResponse(visitRepository.save(visit));
    }

    @Override
    public VisitListResponse getVisits(UUID lbp, Pageable pageable) {
        Page<Visit> visits = visitRepository.getAllVisitsByPatientLbp(lbp, pageable);
        List<VisitResponse> responseList = visits.map(visitMapper::modelToVisitResponse)
                .stream()
                .collect(Collectors.toList());

        return new VisitListResponse(responseList, visits.getTotalElements());
    }
}
