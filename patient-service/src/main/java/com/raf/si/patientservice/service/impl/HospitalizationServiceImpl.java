package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.DischargeRequest;
import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.MedicalReportRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.DoctorResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.exception.NotFoundException;
import com.raf.si.patientservice.mapper.HospitalizationMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.*;
import com.raf.si.patientservice.repository.filtering.filter.DischargeFilter;
import com.raf.si.patientservice.repository.filtering.filter.HospitalisedPatientSearchFilter;
import com.raf.si.patientservice.repository.filtering.filter.MedicalReportFilter;
import com.raf.si.patientservice.repository.filtering.filter.PatientConditionFilter;
import com.raf.si.patientservice.repository.filtering.specification.DischargeSpecification;
import com.raf.si.patientservice.repository.filtering.specification.HospitalisedPatientSpecification;
import com.raf.si.patientservice.repository.filtering.specification.MedicalReportSpecification;
import com.raf.si.patientservice.repository.filtering.specification.PatientConditionSpecification;
import com.raf.si.patientservice.service.HospitalizationService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.HttpUtils;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
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
    private final MedicalReportRepository medicalReportRepository;
    private final DischargeListRepository dischargeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public HospitalizationServiceImpl(HospitalizationRepository hospitalizationRepository,
                                      HospitalRoomRepository hospitalRoomRepository,
                                      HospitalizationMapper hospitalizationMapper,
                                      PatientService patientService,
                                      PatientConditionRepository patientConditionRepository,
                                      MedicalReportRepository medicalReportRepository,
                                      DischargeListRepository dischargeRepository) {

        this.hospitalizationRepository = hospitalizationRepository;
        this.hospitalRoomRepository = hospitalRoomRepository;
        this.hospitalizationMapper = hospitalizationMapper;
        this.patientService = patientService;
        this.patientConditionRepository = patientConditionRepository;
        this.medicalReportRepository = medicalReportRepository;
        this.dischargeRepository = dischargeRepository;
    }

    @Transactional
    @Override
    public HospitalizationResponse hospitalize(HospitalizationRequest request, String token) {
        HospitalRoom hospitalRoom = findHospitalRoom(request.getHospitalRoomId());
        if(request.getDiagnosis().equals("COVID")){
            if(!hospitalRoom.getCovid()){
                throw new BadRequestException("Mora da bude COVID soba.");
            }
        }
        checkCapacity(hospitalRoom);
        entityManager.lock(hospitalRoom, LockModeType.PESSIMISTIC_READ);

        Patient patient = patientService.findPatient(request.getLbp());
        checkPatientAlreadyHospitalized(patient);

        Hospitalization hospitalization = hospitalizationMapper.hospitalizationRequestToHospitalization(
                request,
                hospitalRoom,
                patient
        );

        //FIXME Po novoj specifikaciji može bez uputa ako je COVID.
        if(request.getReferralId() != null){
            updateReferralStatus(request.getReferralId(), token);
        }else if(!request.getDiagnosis().equals("COVID")){
            //FIXME Da li pustiti bez uputa ako nije covid dijagnoza?
            throw new BadRequestException("Ne može bez uputa ako nije covid.");
        }

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
                                                                    String lastName, String jmbg, String covid, Pageable pageable) {
        log.info("Dohvatanje hospitalizovanih pacijenata po odeljenju..");
        HospitalisedPatientSearchFilter filter = new HospitalisedPatientSearchFilter(lbp, pbo, firstName, lastName, jmbg, covid,null);
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
    public HospPatientByHospitalListResponse getHospitalisedPatientsByHospital(String token, UUID pbb, UUID lbp, String firstName,
                                                                               String lastName, String jmbg, String respirator,
                                                                               String imunizovan, String covid, Pageable pageable) {
        log.info("Dohvatanje hospitalizovanih pacijenata po bolnici..");
        List<DepartmentResponse> departmentResponses = getDepartmentsByHospital(pbb, token);
        List<DoctorResponse> doctorResponseList = getDoctorsResponse(token);
        HospitalisedPatientSearchFilter filter = new HospitalisedPatientSearchFilter(
                lbp, null, firstName, lastName, jmbg, covid,
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

        Patient patient = getHospitalisedPatientByLbp(lbp);

        if (patientConditionRequest.getCollectedInfoDate() == null) {
            patientConditionRequest.setCollectedInfoDate(new Date());
        } else {
            checkDateInPast(patientConditionRequest.getCollectedInfoDate());
        }

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

    @Override
    public Patient getHospitalisedPatientByLbp(UUID lbp) {
        return hospitalizationRepository.getHospitalizedPatient(lbp).orElseThrow(() -> {
            log.error("Ne postoji hospitalizovan pacijent sa lbp '{}'", lbp);
            throw new NotFoundException("Ne postoji hospitalizovan pacijent sa datim lbp-om");
        });
    }

    @Override
    public MedicalReportResponse createMedicalReport(UUID lbp, MedicalReportRequest request) {
        Patient patient = getHospitalisedPatientByLbp(lbp);

        log.info("Kreiranje lekarskog izvestaja...");
        MedicalReport medicalReport = medicalReportRepository.save(
                hospitalizationMapper.medicalReportRequestToMedicalReport(patient, request,
                        TokenPayloadUtil.getTokenPayload().getLbz(), isDoctorPOV())
        );

        return hospitalizationMapper.medicalReportToMedicalReportResponse(medicalReport);
    }

    @Override
    public MedicalReportListResponse getMedicalReports(UUID lbp, Date from, Date to, String covid, Pageable pageable) {
        log.info("Getting medical reports from date '{}' to date '{}' for patient with lbp '{}'",
                from, to, lbp);
        MedicalReportFilter filter = new MedicalReportFilter(lbp, from, to, isDoctorPOV(), covid);
        MedicalReportSpecification specification = new MedicalReportSpecification(filter);
        Page<MedicalReport> medicalReports = medicalReportRepository.findAll(specification, pageable);

        return new MedicalReportListResponse(
                medicalReports.map(hospitalizationMapper::medicalReportToMedicalReportResponse)
                        .stream()
                        .collect(Collectors.toList()),
                medicalReports.getTotalElements()
        );
    }

    @Transactional
    @Override
    public DischargeResponse createDischarge(UUID lbp, DischargeRequest request, String token) {
        Hospitalization hospitalization = getHospitalizationByLbp(lbp);
        HospitalRoom hospitalRoom = hospitalization.getHospitalRoom();
        DoctorResponse headOfDepartment = getHeadOfDepartment(TokenPayloadUtil.getTokenPayload().getPbo(), token);

        entityManager.lock(hospitalRoom, LockModeType.PESSIMISTIC_READ);
        entityManager.lock(hospitalization, LockModeType.PESSIMISTIC_READ);

        hospitalRoom.decrementOccupation();
        hospitalization.setDischargeDate(new Date());

        hospitalRoomRepository.save(hospitalRoom);
        hospitalizationRepository.save(hospitalization);

        DischargeList dischargeList = hospitalizationMapper.dischargeListRequestToModel(request, hospitalization,
                TokenPayloadUtil.getTokenPayload().getLbz(), headOfDepartment.getLbz());

        dischargeList = dischargeRepository.save(dischargeList);

        return hospitalizationMapper.modelToDischargeResponse(
                dischargeList,
                hospitalizationMapper.tokenPayloadToUserResponse(TokenPayloadUtil.getTokenPayload()),
                headOfDepartment
        );
    }

    @Override
    public DischargeListResponse getDischarge(UUID lbp, Date dateFrom, Date dateTo, String covid,
                                              Pageable pageable, String token) {
        DischargeFilter filter = new DischargeFilter(lbp, dateFrom, dateTo, covid);
        DischargeSpecification specification = new DischargeSpecification(filter);
        List<DoctorResponse> doctorResponses = getDoctorsResponse(token);
        Page<DischargeList> dischargeLists = dischargeRepository.findAll(specification, pageable);
        return hospitalizationMapper.modelToDischargeListResponse(dischargeLists, doctorResponses);
    }

    private Hospitalization getHospitalizationByLbp(UUID lbp) {
        return hospitalizationRepository.getHospitalizationByLbp(lbp)
                .orElseThrow(() -> {
                    log.error("Ne postoji hospitalizovan pacijent sa lbp '{}'", lbp);
                    throw new NotFoundException("Ne postoji hospitalizovan pacijent sa datim lbp-om");
                });
    }

    private DoctorResponse getHeadOfDepartment(UUID pbo, String token) {
        try {
            return HttpUtils.getHeadOfDepartment(pbo, token).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            if (e instanceof HttpClientErrorException && ((HttpClientErrorException) e).getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException(e.getMessage());
            }
            throw new InternalServerErrorException(e.getMessage());
        }
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

    private boolean isDoctorPOV() {
        return TokenPayloadUtil.getTokenPayload()
                .getPermissions()
                .contains("ROLE_DR_SPEC_POV");
    }

    private void checkDateInPast(Date date) {
        Date currDate = new Date();
        //Zbog vremenskih zona, salje se u UTC
        date = DateUtils.addHours(date, -2);
        if (date.after(currDate)) {
            String errMessage = String.format("Datum '%s' je u buducnosti", date);
            log.error(errMessage);
            throw new BadRequestException(errMessage);
        }
    }
}
