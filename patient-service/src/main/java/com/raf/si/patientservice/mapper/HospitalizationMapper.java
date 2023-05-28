package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.response.HospitalizationResponse;
import com.raf.si.patientservice.model.HospitalRoom;
import com.raf.si.patientservice.model.Hospitalization;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.utils.TokenPayload;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

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
}
