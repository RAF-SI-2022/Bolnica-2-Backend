package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.HospPatientByHospitalResponse;
import com.raf.si.patientservice.dto.response.HospitalisedPatientsResponse;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import com.raf.si.patientservice.dto.response.PatientConditionResponse;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.DoctorResponse;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.model.Hospitalization;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.PatientCondition;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class HospitalizationMapper {

    public Hospitalization hospitalizationRequestToHospitalization(HospitalizationRequest request,
                                                                   HospitalRoom hospitalRoom,
                                                                   Patient patient) {
        Hospitalization hospitalization = new Hospitalization();

        hospitalization.setHospitalRoom(hospitalRoom);
        hospitalization.setPatient(patient);
        hospitalization.setDiagnosis(request.getDiagnosis());
        hospitalization.setNote(request.getNote());
        hospitalization.setDoctorLBZ(request.getSpecialistLbz());
        hospitalization.setReceiptDate(new Date());

        TokenPayload tokenPayload = TokenPayloadUtil.getTokenPayload();
        hospitalization.setRegisterLbz(tokenPayload.getLbz());

        return hospitalization;
    }

    public HospitalizationResponse hospitalizationToResponse(Hospitalization hospitalization,
                                                             HospitalRoom hospitalRoom,
                                                             Patient patient) {

        HospitalizationResponse response = new HospitalizationResponse();

        response.setLbp(patient.getLbp());
        response.setHospitalRoomId(hospitalRoom.getId());

        response.setId(hospitalization.getId());
        response.setDiagnosis(hospitalization.getDiagnosis());
        response.setNote(hospitalization.getNote());
        response.setReceiptDate(hospitalization.getReceiptDate());
        response.setDischargeDate(hospitalization.getDischargeDate());
        response.setRegisterLbz(hospitalization.getRegisterLbz());
        response.setDoctorLbz(hospitalization.getDoctorLBZ());

        return response;
    }

    public HospitalisedPatientsResponse hospitalizationToHospitalisedPatient(Hospitalization hospitalization, List<DoctorResponse> doctors) {
        HospitalisedPatientsResponse response = new HospitalisedPatientsResponse();
        response.setHospitalRoomId(hospitalization.getHospitalRoom().getId());
        response.setRoomNumber(hospitalization.getHospitalRoom().getRoomNumber());
        response.setRoomCapacity(hospitalization.getHospitalRoom().getCapacity());
        response.setLbp(hospitalization.getPatient().getLbp());
        response.setPatientFirstName(hospitalization.getPatient().getFirstName());
        response.setPatientLastName(hospitalization.getPatient().getLastName());
        response.setBirthDate(hospitalization.getPatient().getBirthDate());
        response.setJmbg(hospitalization.getPatient().getJmbg());
        response.setReceiptDate(hospitalization.getReceiptDate());
        response.setDiagnosis(hospitalization.getDiagnosis());
        response.setNote(hospitalization.getNote());
        DoctorResponse doctor = doctors.stream()
                .filter(d -> d.getLbz().equals(hospitalization.getDoctorLBZ()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Cannot find doctor with lbz {} for hospitalization with id {}",
                            hospitalization.getDoctorLBZ(), hospitalization.getId());
                    throw new InternalServerErrorException("Greska na serveru");
                });
        response.setDoctorFirstName(doctor.getFirstName());
        response.setDoctorLastName(doctor.getLastName());

        return response;
    }

    public HospPatientByHospitalResponse hospitalizationToHospPatientByHospitalResponse(Hospitalization hospitalization,
                                                                                        List<DoctorResponse> doctors,
                                                                                        List<DepartmentResponse> departmentResponses) {
        HospPatientByHospitalResponse response = new HospPatientByHospitalResponse();
        response.setHospitalRoomId(hospitalization.getHospitalRoom().getId());
        response.setRoomNumber(hospitalization.getHospitalRoom().getRoomNumber());
        UUID pbo = hospitalization.getHospitalRoom().getPbo();
        response.setPbo(pbo);
        response.setDepartmentName(
                Objects.requireNonNull(
                        departmentResponses.stream()
                                .filter(d -> d.getPbo().equals(pbo))
                                .findFirst()
                                .orElse(null))
                        .getName());
        response.setLbp(hospitalization.getPatient().getLbp());
        response.setPatientFirstName(hospitalization.getPatient().getFirstName());
        response.setPatientLastName(hospitalization.getPatient().getLastName());
        response.setBirthDate(hospitalization.getPatient().getBirthDate());
        response.setJmbg(hospitalization.getPatient().getJmbg());
        response.setReceiptDate(hospitalization.getReceiptDate());
        response.setDiagnosis(hospitalization.getDiagnosis());
        response.setNote(hospitalization.getNote());
        DoctorResponse doctor = doctors.stream()
                .filter(d -> d.getLbz().equals(hospitalization.getDoctorLBZ()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Cannot find doctor with lbz {} for hospitalization with id {}",
                            hospitalization.getDoctorLBZ(), hospitalization.getId());
                    throw new InternalServerErrorException("Greska na serveru");
                });
        response.setDoctorFirstName(doctor.getFirstName());
        response.setDoctorLastName(doctor.getLastName());

        return response;
    }

    public PatientCondition patientConditionRequestToPatientCondition(Patient patient, UUID registerLbz,
                                                                      PatientConditionRequest patientConditionRequest) {
        PatientCondition patientCondition = new PatientCondition();

        patientCondition.setPatient(patient);
        patientCondition.setRegisterLbz(registerLbz);
        patientCondition.setDescription(patientConditionRequest.getDescription());
        patientCondition.setAppliedTherapies(patientConditionRequest.getAppliedTherapies());
        patientCondition.setBloodPressure(patientConditionRequest.getBloodPressure());
        patientCondition.setCollectedInfoDate(patientConditionRequest.getCollectedInfoDate());
        patientCondition.setPulse(patientConditionRequest.getPulse());
        patientCondition.setTemperature(patientConditionRequest.getTemperature());

        return patientCondition;
    }

    public PatientConditionResponse patientConditionToPatientConditionResponse(PatientCondition patientCondition) {
        PatientConditionResponse response = new PatientConditionResponse();

        response.setBloodPressure(patientCondition.getBloodPressure());
        response.setAppliedTherapies(patientCondition.getAppliedTherapies());
        response.setDescription(patientCondition.getDescription());
        response.setPulse(patientCondition.getPulse());
        response.setId(patientCondition.getId());
        response.setLbp(patientCondition.getPatient().getLbp());
        response.setCollectedInfoDate(patientCondition.getCollectedInfoDate());
        response.setTemperature(patientCondition.getTemperature());
        response.setRegisterLbz(patientCondition.getRegisterLbz());

        return response;
    }
}
