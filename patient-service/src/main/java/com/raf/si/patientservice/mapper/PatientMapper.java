package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.request.PatientRequest;
import com.raf.si.patientservice.dto.response.PatientListResponse;
import com.raf.si.patientservice.dto.response.PatientResponse;
import com.raf.si.patientservice.exception.BadRequestException;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.enums.patient.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PatientMapper {

    public Patient patientRequestToPatient(Patient patient, PatientRequest patientRequest){
        patient.setJmbg(patientRequest.getJmbg());
        patient.setFirstName(patientRequest.getFirstName());
        patient.setParentName(patientRequest.getParentName());
        patient.setLastName(patientRequest.getLastName());
        patient.setBirthDate(patientRequest.getBirthDate());
        patient.setBirthplace(patientRequest.getBirthplace());

        if(patientRequest.getDeathDate() != null)
            patient.setDeathDate(patientRequest.getDeathDate());
        if(patientRequest.getAddress() != null)
            patient.setAddress(patientRequest.getAddress());
        if(patientRequest.getPlaceOfLiving() != null)
            patient.setPlaceOfLiving(patientRequest.getPlaceOfLiving());
        if(patientRequest.getPhoneNumber() != null)
            patient.setPhoneNumber(patientRequest.getPhoneNumber());
        if(patientRequest.getEmail() != null)
            patient.setEmail(patientRequest.getEmail());
        if(patientRequest.getCustodianJmbg() != null)
            patient.setCustodianJmbg(patientRequest.getCustodianJmbg());
        if(patientRequest.getCustodianName() != null)
            patient.setCustodianName(patientRequest.getCustodianName());
        if(patientRequest.getChildrenNum() != null)
            patient.setChildrenNum(patientRequest.getChildrenNum());
        if(patient.getProfession() != null)
            patient.setProfession(patientRequest.getProfession());

        Gender gender = Gender.valueOfNotation(patientRequest.getGender());
        if(gender == null){
            String errMessage = String.format("Nepoznat pol '%s'", patientRequest.getGender());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        CountryCode citizenshipCountry = CountryCode.forName(patientRequest.getCitizenshipCountry());
        if(citizenshipCountry == null){
            String errMessage = String.format("Nepoznata drzava '%s'", patientRequest.getCitizenshipCountry());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        CountryCode countryOfLiving = CountryCode.forName(patientRequest.getCountryOfLiving());
        if(countryOfLiving == null){
            String errMessage = String.format("Nepoznata drzava '%s'", patientRequest.getCountryOfLiving());
            log.info(errMessage);
            throw new BadRequestException(errMessage);
        }

        patient.setGender(gender);
        patient.setCitizenshipCountry(citizenshipCountry);
        patient.setCountryOfLiving(countryOfLiving);

        String familyStatusStr = patientRequest.getFamilyStatus();
        if(familyStatusStr != null && familyStatusStr.trim().length() > 0){
            FamilyStatus familyStatus = FamilyStatus.valueOfNotation(familyStatusStr);

            if(familyStatus == null){
                String errMessage = String.format("Nepoznat porodični status '%s'", familyStatusStr);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }

            patient.setFamilyStatus(familyStatus);
        }

        String maritalStatusStr = patientRequest.getMaritalStatus();
        if(maritalStatusStr != null && maritalStatusStr.trim().length() > 0){
            MaritalStatus maritalStatus = MaritalStatus.valueOfNotation(maritalStatusStr);

            if(maritalStatus == null){
                String errMessage = String.format("Nepoznat bračni status '%s'", maritalStatusStr);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }

            patient.setMaritalStatus(maritalStatus);
        }

        String educationStr = patientRequest.getEducation();
        if(educationStr != null && educationStr.trim().length() > 0){
            Education education = Education.valueOfNotation(educationStr);

            if(education == null){
                String errMessage = String.format("Nepoznata stručna sprema '%s'", educationStr);
                log.info(errMessage);
                throw new BadRequestException(errMessage);
            }

            patient.setEducation(education);
        }

        return patient;
    }

    public PatientResponse patientToPatientResponse(Patient patient){
        PatientResponse patientResponse = new PatientResponse();

        patientResponse.setId(patient.getId());
        patientResponse.setJmbg(patient.getJmbg());
        patientResponse.setLbp(patient.getLbp());
        patientResponse.setFirstName(patient.getFirstName());
        patientResponse.setParentName(patient.getParentName());
        patientResponse.setLastName(patient.getLastName());
        patientResponse.setGender(patient.getGender());
        patientResponse.setBirthDate(patient.getBirthDate());
        patientResponse.setDeathDate(patient.getDeathDate());
        patientResponse.setBirthplace(patient.getBirthplace());
        patientResponse.setBirthplace(patient.getBirthplace());
        patientResponse.setCitizenshipCountry(patient.getCitizenshipCountry());
        patientResponse.setAddress(patient.getAddress());
        patientResponse.setPlaceOfLiving(patient.getPlaceOfLiving());
        patientResponse.setCountryOfLiving(patient.getCountryOfLiving());
        patientResponse.setPhoneNumber(patient.getPhoneNumber());
        patientResponse.setEmail(patient.getEmail());
        patientResponse.setCustodianJmbg(patient.getCustodianJmbg());
        patientResponse.setCustodianName(patient.getCustodianName());
        patientResponse.setFamilyStatus(patient.getFamilyStatus());
        patientResponse.setMaritalStatus(patient.getMaritalStatus());
        patientResponse.setChildrenNum(patient.getChildrenNum());
        patientResponse.setEducation(patient.getEducation());
        patientResponse.setProfession(patient.getProfession());
        patientResponse.setDeleted(patient.getDeleted());
        patientResponse.setHealthRecordId(patient.getHealthRecord().getId());

        return patientResponse;
    }

    public PatientListResponse patientPageToPatientListResponse(Page<Patient> patientPage){
        List<PatientResponse> patients = patientPage.toList()
                .stream()
                .map(this::patientToPatientResponse)
                .collect(Collectors.toList());

        return new PatientListResponse(patients, patientPage.getTotalElements());
    }
}
