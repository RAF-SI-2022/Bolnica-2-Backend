package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.response.AllergyResponse;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.VaccinationResponse;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class HealthRecordMapper {

    public HealthRecordResponse healthRecordToHealthRecordResponse(Patient patient,
                                                                   HealthRecord healthRecord,
                                                                   Set<Allergy> allergies,
                                                                   Set<Vaccination> vaccinations,
                                                                   Set<MedicalExamination> examinations,
                                                                   Set<MedicalHistory> medicalHistory,
                                                                   Set<Operation> operations){

        HealthRecordResponse response = new HealthRecordResponse();
        response.setPatientLbp(patient.getLbp());

        response.setId(healthRecord.getId());
        response.setBloodType(healthRecord.getBloodType());
        response.setRegistrationDate(healthRecord.getRegistrationDate());
        response.setRhFactor(healthRecord.getRhFactor());
        if(!operations.isEmpty())
            response.setOperations(operations);

        Set<AllergyResponse> allergyResponseSet = new HashSet<>();
        for(Allergy allergy: allergies){
            AllergyResponse allergyResponse = new AllergyResponse(allergy.getId(),
                    allergy.getAllergen(),
                    healthRecord.getId());
            allergyResponseSet.add(allergyResponse);
        }
        if(!allergyResponseSet.isEmpty())
            response.setAllergies(allergyResponseSet);

        Set<VaccinationResponse> vaccinationResponseSet = new HashSet<>();
        for(Vaccination vaccination: vaccinations){
            VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(),
                    vaccination.getVaccine(),
                    healthRecord.getId(),
                    vaccination.getVaccinationDate());
            vaccinationResponseSet.add(vaccinationResponse);
        }
        if(!vaccinationResponseSet.isEmpty())
            response.setVaccinations(vaccinationResponseSet);

        boolean confidentialPermission = TokenPayloadUtil.getTokenPayload()
                .getPermissions()
                .stream()
                .anyMatch(perm -> perm.equals("ROLE_DR_SPEC_POV"));

        Set<MedicalHistory> medicalHistorySet = new HashSet<>();
        for(MedicalHistory history: medicalHistory){
            if(history.getConfidential() && !confidentialPermission)
                continue;

            medicalHistorySet.add(history);
        }
        if(!medicalHistorySet.isEmpty())
            response.setMedicalHistory(medicalHistorySet);

        Set<MedicalExamination> medicalExaminationSet = new HashSet<>();
        for(MedicalExamination examination: examinations){
            if(examination.getConfidential() && !confidentialPermission)
                continue;

            medicalExaminationSet.add(examination);
        }
        if(!medicalExaminationSet.isEmpty())
            response.setMedicalExaminations(medicalExaminationSet);

        return response;
    }
}
