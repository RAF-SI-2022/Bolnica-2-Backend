package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.DischargeRequest;
import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.MedicalReportRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.DoctorResponse;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
        response.setImmunized(hospitalization.getPatient().getImmunized());
        //FIXME ako nije kreiran condition nema sta da cita
        if(hospitalization.getPatient().getConditions().size() != 0) {
            response.setOnRespirator(hospitalization.getPatient().getConditions().get(
                    hospitalization.getPatient().getConditions().size() - 1
            ).getOnRespirator());
        }
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
        if (patientConditionRequest.getOnRespirator() != null) {
            patientCondition.setOnRespirator(patientConditionRequest.getOnRespirator());
        }

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
        response.setOnRespirator(patientCondition.getOnRespirator());

        return response;
    }

    public MedicalReport medicalReportRequestToMedicalReport(Patient patient, MedicalReportRequest request,
                                                             UUID doctorLbz, boolean isDoctorPOV) {
        MedicalReport medicalReport = new MedicalReport();
        medicalReport.setAdvice(request.getAdvice());
        medicalReport.setDiagnosis(request.getDiagnosis());
        medicalReport.setDoctorLBZ(doctorLbz);
        medicalReport.setPatient(patient);
        medicalReport.setObjectiveResult(request.getObjectiveResult());
        medicalReport.setProposedTherapy(request.getProposedTherapy());

        if (request.getConfidentIndicator() && isDoctorPOV) {
            medicalReport.setConfidentIndicator(true);
        }

        return medicalReport;
    }

    public MedicalReportResponse medicalReportToMedicalReportResponse(MedicalReport medicalReport) {
        MedicalReportResponse response = new MedicalReportResponse();
        response.setAdvice(medicalReport.getAdvice());
        response.setDate(medicalReport.getDate());
        response.setObjectiveResult(medicalReport.getObjectiveResult());
        response.setProposedTherapy(medicalReport.getProposedTherapy());
        response.setConfidentIndicator(medicalReport.getConfidentIndicator());
        response.setDiagnosis(medicalReport.getDiagnosis());
        response.setId(medicalReport.getId());
        response.setLbp(medicalReport.getPatient().getLbp());
        response.setDoctorLbz(medicalReport.getDoctorLBZ());

        return response;
    }

    public DischargeList dischargeListRequestToModel(DischargeRequest request, Hospitalization hospitalization,
                                                     UUID prescribingDoctor, UUID headOfDepartment) {
        DischargeList dischargeList = new DischargeList();

        dischargeList.setHospitalization(hospitalization);
        dischargeList.setAnamnesis(request.getAnamnesis());
        dischargeList.setAttendDiagnoses(request.getAttendDiagnoses());
        dischargeList.setConclusion(request.getConclusion());
        dischargeList.setCourseDisease(request.getCourseDisease());
        dischargeList.setHeadDepartment(headOfDepartment);
        dischargeList.setPrescribingDoctor(prescribingDoctor);
        dischargeList.setPatient(hospitalization.getPatient());
        dischargeList.setTherapy(request.getTherapy());

        return dischargeList;
    }

    public DischargeResponse modelToDischargeResponse(DischargeList dischargeList, DoctorResponse prescribingDoctor,
                                                      DoctorResponse headOfDepartment) {
        DischargeResponse response = new DischargeResponse();
        response.setId(dischargeList.getId());
        response.setLbp(dischargeList.getPatient().getLbp());
        response.setHospitalizationId(dischargeList.getHospitalization().getId());
        response.setReceiptDate(dischargeList.getHospitalization().getReceiptDate());
        response.setDischargeDate(dischargeList.getHospitalization().getDischargeDate());
        response.setDiagnosis(dischargeList.getHospitalization().getDiagnosis());
        response.setAttendDiagnoses(dischargeList.getAttendDiagnoses());
        response.setAnamnesis(dischargeList.getAnamnesis());
        response.setCourseDisease(dischargeList.getCourseDisease());
        response.setConclusion(dischargeList.getConclusion());
        response.setTherapy(dischargeList.getTherapy());
        response.setDoctor(prescribingDoctor);
        response.setHeadOfDepartment(headOfDepartment);
        response.setDate(dischargeList.getDate());

        return response;
    }

    public DischargeListResponse modelToDischargeListResponse(Page<DischargeList> dischargeListPage, List<DoctorResponse> doctorResponses) {
        List<DischargeResponse> responseList = dischargeListPage.map(d -> {
            DoctorResponse doctor = doctorResponses.stream()
                    .filter(dr -> dr.getLbz().equals(d.getPrescribingDoctor()))
                    .findFirst()
                    .orElse(null);
            DoctorResponse headOfDepartment = doctorResponses.stream()
                    .filter(dr -> dr.getLbz().equals(d.getHeadDepartment()))
                    .findFirst()
                    .orElse(null);
            return modelToDischargeResponse(d, doctor, headOfDepartment);
        }).stream().collect(Collectors.toList());

        return new DischargeListResponse(responseList, dischargeListPage.getTotalElements());
    }

    public DoctorResponse tokenPayloadToUserResponse(TokenPayload tokenPayload) {
        DoctorResponse doctorResponse = new DoctorResponse();
        doctorResponse.setLbz(tokenPayload.getLbz());
        doctorResponse.setFirstName(tokenPayload.getFirstName());
        doctorResponse.setLastName(tokenPayload.getLastName());

        return doctorResponse;
    }
}
