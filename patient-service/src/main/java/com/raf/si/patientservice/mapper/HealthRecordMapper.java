package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.response.AllergyResponse;
import com.raf.si.patientservice.dto.response.HealthRecordResponse;
import com.raf.si.patientservice.dto.response.LightHealthRecordResponse;
import com.raf.si.patientservice.dto.response.VaccinationResponse;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class HealthRecordMapper {

    public HealthRecordResponse healthRecordToHealthRecordResponse(Patient patient,
                                                                   HealthRecord healthRecord,
                                                                   List<Allergy> allergies,
                                                                   List<Vaccination> vaccinations,
                                                                   List<MedicalExamination> examinations,
                                                                   List<MedicalHistory> medicalHistory,
                                                                   List<Operation> operations){

        HealthRecordResponse response = new HealthRecordResponse();
        response.setPatientLbp(patient.getLbp());

        response.setId(healthRecord.getId());
        response.setBloodType(healthRecord.getBloodType());
        response.setRegistrationDate(healthRecord.getRegistrationDate());
        response.setRhFactor(healthRecord.getRhFactor());
        if(!operations.isEmpty())
            response.setOperations(operations);

        List<AllergyResponse> allergyResponseList = makeAllergyResponse(allergies, healthRecord);
        if(!allergyResponseList.isEmpty())
            response.setAllergies(allergyResponseList);

        List<VaccinationResponse> vaccinationResponseList = makeVaccinationResponse(vaccinations, healthRecord);
        if(!vaccinationResponseList.isEmpty())
            response.setVaccinations(vaccinationResponseList);

        List<MedicalHistory> medicalHistoryList = makeMedicalHistoryList(medicalHistory);
        if(!medicalHistoryList.isEmpty())
            response.setMedicalHistory(medicalHistoryList);

        List<MedicalExamination> medicalExaminationList = makeMedicalExaminationList(examinations);
        if(!medicalExaminationList.isEmpty())
            response.setMedicalExaminations(medicalExaminationList);

        return response;
    }

    public LightHealthRecordResponse healthRecordToLightHealthRecordResponse(Patient patient,
                                                                             HealthRecord healthRecord,
                                                                             List<Allergy> allergies,
                                                                             List<Vaccination> vaccinations) {

        LightHealthRecordResponse response = new LightHealthRecordResponse();
        response.setId(healthRecord.getId());
        response.setRhFactor(healthRecord.getRhFactor());
        response.setBloodType(healthRecord.getBloodType());
        response.setPatientLbp(patient.getLbp());

        List<VaccinationResponse> vaccinationResponseList = makeVaccinationResponse(vaccinations, healthRecord);
        if(!vaccinationResponseList.isEmpty())
            response.setVaccinations(vaccinationResponseList);

        List<AllergyResponse> allergyResponseList = makeAllergyResponse(allergies, healthRecord);
        if(!allergyResponseList.isEmpty())
            response.setAllergies(allergyResponseList);

        return response;
    }

    private List<VaccinationResponse> makeVaccinationResponse(List<Vaccination> vaccinations, HealthRecord healthRecord){
        List<VaccinationResponse> vaccinationResponseList = new ArrayList<>();

        for(Vaccination vaccination: vaccinations){
            VaccinationResponse vaccinationResponse = new VaccinationResponse(vaccination.getId(),
                    vaccination.getVaccine(),
                    healthRecord.getId(),
                    vaccination.getVaccinationDate());
            vaccinationResponseList.add(vaccinationResponse);
        }

        return vaccinationResponseList;
    }

    private List<AllergyResponse> makeAllergyResponse(List<Allergy> allergies, HealthRecord healthRecord){
        List<AllergyResponse> allergyResponseList = new ArrayList<>();

        for(Allergy allergy: allergies){
            AllergyResponse allergyResponse = new AllergyResponse(allergy.getId(),
                    allergy.getAllergen(),
                    healthRecord.getId());
            allergyResponseList.add(allergyResponse);
        }

        return allergyResponseList;
    }

    private List<MedicalHistory> makeMedicalHistoryList(List<MedicalHistory> medicalHistory){
        boolean confidentialPermission = getConfidentialPermission();
        List<MedicalHistory> medicalHistoryList = new ArrayList<>();

        for(MedicalHistory history: medicalHistory){
            if(history.getConfidential() && !confidentialPermission)
                continue;

            medicalHistoryList.add(history);
        }

        return medicalHistoryList;
    }

    private List<MedicalExamination> makeMedicalExaminationList(List<MedicalExamination> examinations){
        boolean confidentialPermission = getConfidentialPermission();
        List<MedicalExamination> medicalExaminationList = new ArrayList<>();

        for(MedicalExamination examination: examinations){
            if(examination.getConfidential() && !confidentialPermission)
                continue;

            medicalExaminationList.add(examination);
        }

        return medicalExaminationList;
    }

    private boolean getConfidentialPermission(){
        return TokenPayloadUtil.getTokenPayload()
                .getPermissions()
                .stream()
                .anyMatch(perm -> perm.equals("ROLE_DR_SPEC_POV"));
    }
}
