package com.raf.si.laboratoryservice.service.impl;

import com.raf.si.laboratoryservice.dto.request.CreateReferralRequest;
import com.raf.si.laboratoryservice.dto.response.*;
import com.raf.si.laboratoryservice.exception.BadRequestException;
import com.raf.si.laboratoryservice.exception.InternalServerErrorException;
import com.raf.si.laboratoryservice.exception.NotFoundException;
import com.raf.si.laboratoryservice.mapper.ReferralMapper;
import com.raf.si.laboratoryservice.model.LabWorkOrder;
import com.raf.si.laboratoryservice.model.Referral;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.repository.LabWorkOrderRepository;
import com.raf.si.laboratoryservice.repository.ReferralRepository;
import com.raf.si.laboratoryservice.service.ReferralService;
import com.raf.si.laboratoryservice.utils.HttpUtils;
import com.raf.si.laboratoryservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;

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
        checkReferralDate(createReferralRequest.getCreationTime());
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
    public List<UnprocessedReferralsResponse> unprocessedReferrals(UUID lbp, String authorizationHeader) {
        UUID pboFromToken = TokenPayloadUtil.getTokenPayload().getPbo();
        System.out.println(pboFromToken);
        List<Referral> unprocessedReferralsByThreeParams = referralRepository.findByLbpAndPboReferredToAndStatus(lbp, pboFromToken, ReferralStatus.NEREALIZOVAN);
        if (unprocessedReferralsByThreeParams.isEmpty()) {
            log.error("Ne postoji trazeni uput");
            throw new NotFoundException("Uput sa zadatim parametrima nije pronadjen");
        }

        List<Referral> unprocessedReferrals = unprocessedReferralsByThreeParams.stream()
                .filter(referral -> referral.getLabWorkOrder() == null)
                .collect(Collectors.toList());

        List<UnprocessedReferralsResponse> unprocessedReferralsResponses = new ArrayList<>();
        List<DepartmentResponse> allDepartments = getDepartments(authorizationHeader);
        List<DoctorResponse> allDoctors = getAllDoctors(authorizationHeader);

        for (Referral referral : unprocessedReferrals) {
            UnprocessedReferralsResponse unprocessedReferral = new UnprocessedReferralsResponse();
            unprocessedReferral.setReferralId(referral.getId());

            for (DepartmentResponse departmentResponse: allDepartments) {
                if (referral.getPboReferredFrom().equals(departmentResponse.getPbo())) {
                    unprocessedReferral.setDepartmentName(departmentResponse.getName());
                }
            }

            for (DoctorResponse doctor: allDoctors) {
                if (referral.getLbz().equals(doctor.getLbz())) {
                    unprocessedReferral.setDoctorFirstName(doctor.getFirstName());
                    unprocessedReferral.setDoctorLastName(doctor.getLastName());
                }
            }
            unprocessedReferral.setAnalysisParameters(referral.getAnalysisParameters());
            unprocessedReferral.setCreationDate(referral.getCreationTime());
            unprocessedReferral.setComment(referral.getComment());
            unprocessedReferralsResponses.add(unprocessedReferral);
        }

        return unprocessedReferralsResponses;
    }

    private List<DoctorResponse> getAllDoctors(String token) {
        ResponseEntity<List<DoctorResponse>> response;
        List<DoctorResponse> responseBody;
        try {
            response = HttpUtils.getAllDoctors(token);
            responseBody = response.getBody();

        } catch (IllegalArgumentException e) {
            String errMessage = String.format("Error when calling user service: " + e.getMessage());
            log.info(errMessage);
            throw new InternalServerErrorException("Error when calling user service: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                String errMessage = String.format("Nisu pronadjeni doktori");
                log.info(errMessage);
                throw new NotFoundException(errMessage);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errMessage = String.format("Bad request: " + e.getMessage());
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
            throw new InternalServerErrorException("Error when calling user service: " + e.getMessage());
        }
        return responseBody;
    }

    private List<DepartmentResponse> getDepartments(String token) {
        ResponseEntity<List<DepartmentResponse>> response;
        List<DepartmentResponse> responseBody;
        try {
            response = HttpUtils.findDepartmentName(token);
            responseBody = response.getBody();

        } catch (IllegalArgumentException e) {
            String errMessage = String.format("Error when calling user service: " + e.getMessage());
            log.info(errMessage);
            throw new InternalServerErrorException("Error when calling user service: " + e.getMessage());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                String errMessage = String.format("Odeljenje ne postoji");
                log.info(errMessage);
                throw new NotFoundException(errMessage);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String errMessage = String.format("Bad request: " + e.getMessage());
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }
            throw new InternalServerErrorException("Error when calling user service: " + e.getMessage());
        }
        return responseBody;
    }

    private void checkReferralDate(Date scheduledDate) {
        Date currentDate = new Date();
        if (scheduledDate.before(currentDate)) {
            String errMessage = "Uput ne može da se napravi u prošlosti";
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
    }
}
