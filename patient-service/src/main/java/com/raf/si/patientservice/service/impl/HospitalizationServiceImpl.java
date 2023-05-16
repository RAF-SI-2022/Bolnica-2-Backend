package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.mapper.HospitalizationMapper;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.model.Hospitalization;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.repository.HospitalRoomRepository;
import com.raf.si.patientservice.repository.HospitalizationRepository;
import com.raf.si.patientservice.service.HospitalizationService;
import com.raf.si.patientservice.service.PatientService;
import com.raf.si.patientservice.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

@Slf4j
@Service
public class HospitalizationServiceImpl implements HospitalizationService {

    private final HospitalizationRepository hospitalizationRepository;
    private final HospitalRoomRepository hospitalRoomRepository;
    private final HospitalizationMapper hospitalizationMapper;
    private final PatientService patientService;

    @PersistenceContext
    private EntityManager entityManager;

    public HospitalizationServiceImpl(HospitalizationRepository hospitalizationRepository,
                                      HospitalRoomRepository hospitalRoomRepository,
                                      HospitalizationMapper hospitalizationMapper,
                                      PatientService patientService) {

        this.hospitalizationRepository = hospitalizationRepository;
        this.hospitalRoomRepository = hospitalRoomRepository;
        this.hospitalizationMapper = hospitalizationMapper;
        this.patientService = patientService;
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
        } catch(Exception e) {
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
