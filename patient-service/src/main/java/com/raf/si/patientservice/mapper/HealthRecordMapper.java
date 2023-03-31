package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.AddAllergyRequest;
import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.utils.TokenPayloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
                                                                   Page<Allergy> allergies,
                                                                   Page<Vaccination> vaccinations,
                                                                   Page<MedicalExamination> examinations,
                                                                   Page<MedicalHistory> medicalHistory,
                                                                   Page<Operation> operations){

        HealthRecordResponse response = new HealthRecordResponse();
        response.setPatientLbp(patient.getLbp());

        response.setId(healthRecord.getId());
        response.setBloodType(healthRecord.getBloodType());
        response.setRegistrationDate(healthRecord.getRegistrationDate());
        response.setRhFactor(healthRecord.getRhFactor());

        List<Operation> operationList = operations.toList();
        if(!operationList.isEmpty()) {
            OperationListResponse operationResponse = new OperationListResponse(operationList,
                    operations.getTotalElements());
            response.setOperations(operationResponse);
        }

        List<AllergyResponse> allergyResponseList = makeAllergyResponse(allergies.toList(), healthRecord);
        if(!allergyResponseList.isEmpty()) {
            AllergyListResponse allergyResponse = new AllergyListResponse(allergyResponseList,
                    allergies.getTotalElements());
            response.setAllergies(allergyResponse);
        }

        List<VaccinationResponse> vaccinationResponseList = makeVaccinationResponse(vaccinations.toList(), healthRecord);
        if(!vaccinationResponseList.isEmpty()) {
            VaccinationListResponse vaccinationResponse = new VaccinationListResponse(vaccinationResponseList,
                    vaccinations.getTotalElements());
            response.setVaccinations(vaccinationResponse);
        }

        List<MedicalHistory> medicalHistoryList = medicalHistory.toList();
        if(!medicalHistoryList.isEmpty()) {
            MedicalHistoryListResponse historyResponse = new MedicalHistoryListResponse(medicalHistoryList,
                    medicalHistory.getTotalElements());
            response.setMedicalHistory(historyResponse);
        }

        List<MedicalExamination> medicalExaminationList = examinations.toList();
        if(!medicalExaminationList.isEmpty()) {
            MedicalExaminationListResponse examinationsResponse = new MedicalExaminationListResponse(medicalExaminationList,
                    examinations.getTotalElements());
            response.setMedicalExaminations(examinationsResponse);
        }

        return response;
    }

    public LightHealthRecordResponse healthRecordToLightHealthRecordResponse(Patient patient,
                                                                             HealthRecord healthRecord,
                                                                             Page<Allergy> allergies,
                                                                             Page<Vaccination> vaccinations) {

        LightHealthRecordResponse response = new LightHealthRecordResponse();
        response.setId(healthRecord.getId());
        response.setRhFactor(healthRecord.getRhFactor());
        response.setBloodType(healthRecord.getBloodType());
        response.setPatientLbp(patient.getLbp());

        List<VaccinationResponse> vaccinationResponseList = makeVaccinationResponse(vaccinations.toList(), healthRecord);
        if(!vaccinationResponseList.isEmpty()) {
            VaccinationListResponse vaccinationResponse = new VaccinationListResponse(vaccinationResponseList,
                    vaccinations.getTotalElements());
            response.setVaccinations(vaccinationResponse);
        }

        List<AllergyResponse> allergyResponseList = makeAllergyResponse(allergies.toList(), healthRecord);
        if(!allergyResponseList.isEmpty()) {
            AllergyListResponse allergyResponse = new AllergyListResponse(allergyResponseList,
                    allergies.getTotalElements());
            response.setAllergies(allergyResponse);
        }

        return response;
    }

    public MedicalExaminationListResponse getPermittedExaminations(Page<MedicalExamination> examinations){
        return new MedicalExaminationListResponse(examinations.toList(), examinations.getTotalElements());
    }

    public MedicalHistoryListResponse getPermittedMedicalHistory(Page<MedicalHistory> medicalHistory){
        return new MedicalHistoryListResponse(medicalHistory.toList(), medicalHistory.getTotalElements());
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

    public VaccineListResponse vaccineListToVaccineListResponse(List<Vaccine> vaccines) {
        List<VaccineResponse> vaccineResponses = new ArrayList<>();
        for(Vaccine vaccine : vaccines) {
            VaccineResponse vaccineResponse = new VaccineResponse(
                    vaccine.getId(),
                    vaccine.getName(),
                    vaccine.getType(),
                    vaccine.getDescription(),
                    vaccine.getProducer());
            vaccineResponses.add(vaccineResponse);
        }
        return new VaccineListResponse(vaccineResponses);
    }

    public AllergenListResponse allergenListToVaccineListResponse(List<Allergen> allergens) {
        List<AllergenResponse> allergenResponses = new ArrayList<>();
        for(Allergen allergen : allergens) {
            AllergenResponse allergenResponse = new AllergenResponse(
                    allergen.getId(),
                    allergen.getName());
            allergenResponses.add(allergenResponse);
        }
        return new AllergenListResponse(allergenResponses);
    }

    public Vaccination addVaccinationRequestToVaccinatin(AddVaccinationRequest addVaccinationRequest, HealthRecord healthRecord, Vaccine vaccine) {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccine(vaccine);
        vaccination.setDeleted(false);
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());
        vaccination.setHealthRecord(healthRecord);
        return vaccination;
    }

    public Allergy addAllergyRequestToVaccinatin(AddAllergyRequest addAllergyRequest, HealthRecord healthRecord, Allergen allergen) {
        Allergy allergy = new Allergy();
        allergy.setAllergen(allergen);
        allergy.setDeleted(false);
        allergy.setHealthRecord(healthRecord);
        return allergy;
    }

    public HealthRecord updateHealthRecordRequestToHealthRecord(BloodType bloodType, RHFactor rhFactor, HealthRecord healthRecord) {
        healthRecord.setBloodType(bloodType);
        healthRecord.setRhFactor(rhFactor);
        return healthRecord;
    }

}
