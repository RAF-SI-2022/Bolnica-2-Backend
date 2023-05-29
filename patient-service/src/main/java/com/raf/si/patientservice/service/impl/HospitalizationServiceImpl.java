package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.DoctorResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.HospitalizationMapper;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.model.Hospitalization;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.PatientCondition;
import com.raf.si.patientservice.repository.HospitalRoomRepository;
import com.raf.si.patientservice.repository.HospitalizationRepository;
import com.raf.si.patientservice.repository.PatientConditionRepository;
import com.raf.si.patientservice.repository.filtering.filter.HospitalisedPatientSearchFilter;
import com.raf.si.patientservice.repository.filtering.filter.PatientConditionFilter;
import com.raf.si.patientservice.repository.filtering.specification.HospitalisedPatientSpecification;
import com.raf.si.patientservice.repository.filtering.specification.PatientConditionSpecification;
import com.raf.si.patientservice.service.HospitalizationService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HospitalizationServiceImpl implements HospitalizationService {

    private final HospitalizationRepository hospitalizationRepository;
    private final HospitalRoomRepository hospitalRoomRepository;
    private final HospitalizationMapper hospitalizationMapper;
    private final PatientService patientService;
    private final PatientConditionRepository patientConditionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public HospitalizationServiceImpl(HospitalizationRepository hospitalizationRepository,
                                      HospitalRoomRepository hospitalRoomRepository,
                                      HospitalizationMapper hospitalizationMapper,
                                      PatientService patientService,
                                      PatientConditionRepository patientConditionRepository) {

        this.hospitalizationRepository = hospitalizationRepository;
        this.hospitalRoomRepository = hospitalRoomRepository;
        this.hospitalizationMapper = hospitalizationMapper;
        this.patientService = patientService;
        this.patientConditionRepository = patientConditionRepository;
    }

    @Transactional
    @Override
    public HospitalizationResponse hospitalize(HospitalizationRequest request, String token) {
        HospitalRoom hospitalRoom = findHospitalRoom(request.getHospitalRoomId());
        checkCapacity(hospitalRoom);
        entityManager.lock(hospitalRoom, LockModeType.PESSIMISTIC_READ);

        Patient patient = patientService.findPatient(request.getLbp());
        checkPatientAlreadyHospitalized(patient);

        Hospitalization hospitalization = hospitalizationMapper.hospitalizationRequestToHospitalization(
                request,
                hospitalRoom,
                patient
        );

        updateReferralStatus(request.getReferralId(), token);

        hospitalRoom.incrementOccupation();
        hospitalization = hospitalizationRepository.save(hospitalization);
        hospitalRoom = hospitalRoomRepository.save(hospitalRoom);

        return hospitalizationMapper.hospitalizationToResponse(
                hospitalization,
                hospitalRoom,
                patient
        );
    }

    @Override
    public HospitalisedPatientsListResponse getHospitalisedPatients(String token, UUID pbo, UUID lbp, String firstName,
                                                                    String lastName, String jmbg, Pageable pageable) {
        log.info("Dohvatanje hospitalizovanih pacijenata po odeljenju..");
        HospitalisedPatientSearchFilter filter = new HospitalisedPatientSearchFilter(lbp, pbo, firstName, lastName, jmbg, null);
        HospitalisedPatientSpecification spec = new HospitalisedPatientSpecification(filter);
        Page<Hospitalization> hospitalizations = hospitalizationRepository.findAll(spec, pageable);
        List<DoctorResponse> doctorResponseList = getDoctorsResponse(token);
        return new HospitalisedPatientsListResponse(
                hospitalizations.map((h) -> hospitalizationMapper.hospitalizationToHospitalisedPatient(h, doctorResponseList))
                        .stream()
                        .collect(Collectors.toList()),
                hospitalizations.getTotalElements());
    }

    @Override
    public HospPatientByHospitalListResponse getHospitalisedPatientsByHospital(String token, UUID pbb, UUID lbp, String firstName, String lastName, String jmbg, Pageable pageable) {
        log.info("Dohvatanje hospitalizovanih pacijenata po bolnici..");
        List<DepartmentResponse> departmentResponses = getDepartmentsByHospital(pbb, token);
        List<DoctorResponse> doctorResponseList = getDoctorsResponse(token);
        HospitalisedPatientSearchFilter filter = new HospitalisedPatientSearchFilter(
                lbp, null, firstName, lastName, jmbg,
                departmentResponses.stream()
                        .map(DepartmentResponse::getPbo)
                        .collect(Collectors.toList())
        );
        HospitalisedPatientSpecification spec = new HospitalisedPatientSpecification(filter);
        Page<Hospitalization> hospitalizations = hospitalizationRepository.findAll(spec, pageable);

        return new HospPatientByHospitalListResponse(
                hospitalizations.map(
                        h -> hospitalizationMapper.hospitalizationToHospPatientByHospitalResponse(h, doctorResponseList, departmentResponses)
                ).stream()
                        .collect(Collectors.toList()),
                hospitalizations.getTotalElements()
        );
    }

    @Override
    public PatientConditionResponse createPatientCondition(UUID lbp, PatientConditionRequest patientConditionRequest) {
        if (patientConditionRequest.allNull()) {
            log.error("Sva polja za kreiranje stanja pacijenta su null");
            throw new BadRequestException("Barem jedno polje stanja pacijenta ne sme biti null");
        }

        Patient patient = patientService.findPatient(lbp);

        log.info("Kreiranje stanja pacijenta...");
        PatientCondition patientCondition = patientConditionRepository.save(
                hospitalizationMapper.patientConditionRequestToPatientCondition(
                        patient, TokenPayloadUtil.getTokenPayload().getLbz(), patientConditionRequest
                )
        );

        return hospitalizationMapper.patientConditionToPatientConditionResponse(patientCondition);
    }

    @Override
    public PatientConditionListResponse getPatientConditions(UUID lbp, Date dateFrom, Date dateTo, Pageable pageable) {
        PatientConditionFilter filter = new PatientConditionFilter(lbp, dateFrom, dateTo);
        PatientConditionSpecification specification = new PatientConditionSpecification(filter);
        Page<PatientCondition> responseList = patientConditionRepository.findAll(specification, pageable);
        List<PatientConditionResponse> patientConditionResponses = responseList.map(hospitalizationMapper::patientConditionToPatientConditionResponse)
                .stream().collect(Collectors.toList());

        return new PatientConditionListResponse(patientConditionResponses, responseList.getTotalElements());
    }

    private List<DoctorResponse> getDoctorsResponse(String token) {
        try {
            return Arrays.asList(HttpUtils.findDoctors(token).getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private List<DepartmentResponse> getDepartmentsByHospital(UUID pbb, String token) {
        try {
            return Arrays.asList(HttpUtils.findDepartmentsByHospital(pbb, token).getBody());
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException(e.getMessage());
            }
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private HospitalRoom findHospitalRoom(Long id) {
        HospitalRoom hospitalRoom = entityManager.find(HospitalRoom.class, id);
        if (hospitalRoom == null) {
            String errMessage = String.format("Soba sa id-jem %d ne postoji", id);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
        return hospitalRoom;
    }

    private void checkCapacity(HospitalRoom hospitalRoom) {
        if (hospitalRoom.getOccupation() >= hospitalRoom.getCapacity()) {
            String errMessage = String.format("Nema vise mesta u sobi sa id-jem %d", hospitalRoom.getId());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }

    private void updateReferralStatus(Long id, String token) {
        try {
            HttpUtils.changeReferralStatus(id, "Realizovan", token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    private void checkPatientAlreadyHospitalized(Patient patient) {
        if (hospitalizationRepository.patientAlreadyHospitalized(patient)) {
            String errMessage = String.format("Pacijent sa lbp-om %s je vec hospitalizovan", patient.getLbp());
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }
}
