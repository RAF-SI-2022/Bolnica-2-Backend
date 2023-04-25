package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.AddVaccinationRequest;
import com.raf.si.patientservice.dto.request.CreateExaminationReportRequest;
import com.raf.si.patientservice.dto.request.UpdateHealthRecordRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.healthrecord.BloodType;
import com.raf.si.patientservice.model.enums.healthrecord.RHFactor;
import com.raf.si.patientservice.model.enums.medicalhistory.TreatmentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

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

    public BasicHealthRecordResponse healthRecordToBasicHealthRecordResponse(UUID lbp,
                                                                             HealthRecord healthRecord) {

        BasicHealthRecordResponse response = new BasicHealthRecordResponse();
        response.setId(healthRecord.getId());
        response.setRhFactor(healthRecord.getRhFactor());
        response.setBloodType(healthRecord.getBloodType());
        response.setPatientLbp(lbp);
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

    public AllergenListResponse allergenListToAllergenListResponse(List<Allergen> allergens) {
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
        vaccination.setVaccinationDate(addVaccinationRequest.getDate());
        vaccination.setHealthRecord(healthRecord);
        return vaccination;
    }

    public Allergy addAllergyRequestToAllergy(HealthRecord healthRecord, Allergen allergen) {
        Allergy allergy = new Allergy();
        allergy.setAllergen(allergen);
        allergy.setHealthRecord(healthRecord);
        return allergy;
    }

    public HealthRecord updateHealthRecordRequestToHealthRecord(UpdateHealthRecordRequest updateHealthRecordRequest, HealthRecord healthRecord) {

        // proveri da li su vrednosti koje je korisnik poslao dobre
        BloodType bt = BloodType.forName(updateHealthRecordRequest.getBlodtype());
        //log.info(bt.toString());
        if(bt == null) {
            String errMessage = format("Nepoznata krvna grupa '%s'", updateHealthRecordRequest.getBlodtype());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        RHFactor rhf = RHFactor.valueOfNotation(updateHealthRecordRequest.getRhfactor());
        if (rhf == null) {
            String errMessage = format("Nepoznat rh faktor '%s'", updateHealthRecordRequest.getRhfactor());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        healthRecord.setBloodType(bt);
        healthRecord.setRhFactor(rhf);
        return healthRecord;
    }

    public ExtendedVaccinationResponse vaccinationToExtendedVaccinationResponse(HealthRecord healthRecord, Vaccination vaccination) {
        ExtendedVaccinationResponse extendedVaccinationResponse = new ExtendedVaccinationResponse();
        extendedVaccinationResponse.setVaccinationCount((long) healthRecord.getVaccinations().size());
        VaccinationResponse vaccinationResponse = new VaccinationResponse(
                vaccination.getId(),
                vaccination.getVaccine(),
                healthRecord.getId(),
                vaccination.getVaccinationDate()
                );
        extendedVaccinationResponse.setVaccinationResponse(vaccinationResponse);
        return extendedVaccinationResponse;
    }

    public ExtendedAllergyResponse allergyToExtendedAllergyResponse(HealthRecord healthRecord, Allergy allergy) {
        ExtendedAllergyResponse extendedAllergyResponse = new ExtendedAllergyResponse();
        extendedAllergyResponse.setAllergyCount((long) healthRecord.getAllergies().size());
        AllergyResponse allergyResponse = new AllergyResponse(
                allergy.getId(),
                allergy.getAllergen(),
                healthRecord.getId()
        );
        extendedAllergyResponse.setAllergyResponse(allergyResponse);
        return extendedAllergyResponse;
    }

    public MedicalExamination createExaminationReportRequestToExamination(UUID lbz,
                                                                          HealthRecord healthRecord,
                                                                          CreateExaminationReportRequest createExaminationReportRequest,
                                                                          Diagnosis diagnosis) {
        MedicalExamination medicalExamination = new MedicalExamination();
        medicalExamination.setHealthRecord(healthRecord);
        medicalExamination.setDate(new Date());
        medicalExamination.setLbz(lbz);
        if(createExaminationReportRequest.getConfidential() != null){
            medicalExamination.setConfidential(createExaminationReportRequest.getConfidential());
        }
        if(createExaminationReportRequest.getMainSymptoms() != null){
            medicalExamination.setMainSymptoms(createExaminationReportRequest.getMainSymptoms());
        }
        if(createExaminationReportRequest.getAnamnesis() != null){
            medicalExamination.setAnamnesis(createExaminationReportRequest.getAnamnesis());
        }
        if(createExaminationReportRequest.getFamilyAnamnesis() != null){
            medicalExamination.setFamilyAnamnesis(createExaminationReportRequest.getFamilyAnamnesis());
        }
        if(createExaminationReportRequest.getPatientOpinion() != null){
            medicalExamination.setPatientOpinion(createExaminationReportRequest.getPatientOpinion());
        }
        if(createExaminationReportRequest.getCurrentIllness() != null){
            medicalExamination.setCurrentIllness(createExaminationReportRequest.getCurrentIllness());
        }

        medicalExamination.setObjectiveFinding(createExaminationReportRequest.getObjectiveFinding());

        if(createExaminationReportRequest.getSuggestedTherapy() != null){
            medicalExamination.setSuggestedTherapy(createExaminationReportRequest.getSuggestedTherapy());
        }
        if(createExaminationReportRequest.getAdvice() != null){
            medicalExamination.setAdvice(createExaminationReportRequest.getAdvice());
        }
        if(diagnosis != null){
            medicalExamination.setDiagnosis(diagnosis);
        }

        return medicalExamination;
    }

    public MedicalHistory medicalExaminationToMedicalHistory(MedicalExamination medicalExamination,
                                                             HealthRecord healthRecord, MedicalHistory oldMedicalHistory,
                                                             CreateExaminationReportRequest createExaminationReportRequest) {

        MedicalHistory medicalHistory = new MedicalHistory();

        medicalHistory.setConfidential(medicalExamination.getConfidential());

        if(oldMedicalHistory == null){
            medicalHistory.setIllnessStart(new Date());
        }
        else {
            medicalHistory.setIllnessStart(oldMedicalHistory.getIllnessStart());
        }

        if(createExaminationReportRequest.getTreatmentResult() == null) {
            String errMessage = "Polje treatmentResult ne sme biti prazno ako se kreira pregled sa dijagnozom";
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        TreatmentResult treatmentResult = TreatmentResult.valueOfNotation(createExaminationReportRequest.getTreatmentResult());
        if(treatmentResult == null) {
            String errMessage = format("Polje tretmantResult ne moze da bude '%s'", createExaminationReportRequest.getTreatmentResult());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        // postavi iz request-a
        if(oldMedicalHistory != null && !treatmentResult.equals(TreatmentResult.U_TOKU)){
            medicalHistory.setIllnessEnd(new Date());
        }
        medicalHistory.setTreatmentResult(treatmentResult);

        if(createExaminationReportRequest.getCurrentStateDescription() == null) {
            String errMessage = "Polje 'currentStateDescription' ne moze da bude prazno ako se kreira pregled sa dijagnozom";
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }
        medicalHistory.setCurrentStateDescription(createExaminationReportRequest.getCurrentStateDescription());



        medicalHistory.setValidFrom(new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        try {
            date = formatter.parse("31-12-9999.");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        medicalHistory.setValidUntil(date);
        medicalHistory.setValid(true);

        medicalHistory.setDiagnosis(medicalExamination.getDiagnosis());
        medicalHistory.setHealthRecord(healthRecord);

        return medicalHistory;
    }


}
