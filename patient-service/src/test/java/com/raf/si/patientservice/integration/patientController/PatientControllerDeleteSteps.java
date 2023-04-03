package com.raf.si.patientservice.integration.patientController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raf.si.patientservice.integration.CucumberConfig;
import com.raf.si.patientservice.integration.UtilsHelper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.patient.CountryCode;
import com.raf.si.patientservice.model.enums.patient.Gender;
import com.raf.si.patientservice.repository.HealthRecordRepository;
import com.raf.si.patientservice.repository.OperationRepository;
import com.raf.si.patientservice.repository.PatientRepository;
import com.raf.si.patientservice.utils.JwtUtil;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PatientControllerDeleteSteps extends CucumberConfig {

    private final String jmbg = "123456789";

    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private HealthRecordRepository healthRecordRepository;
    @Autowired
    private OperationRepository operationRepository;
    @Autowired
    private JwtUtil jwtUtil;
    private UtilsHelper util;
    private ResultActions resultAction;

    @Before
    public void initialization() {
        util = new UtilsHelper(jwtUtil);
    }

    @Given("Patient exists and has zero or more allergies, operations, medical histories, medical examinations and\\/or")
    public void patient_exists_and_has_zero_or_more_allergies_operations_medical_histories_medical_examinations_and_or() {
        Patient patient = makePatient();
        HealthRecord healthRecord = new HealthRecord();

        patient.setHealthRecord(healthRecord);
        patientRepository.save(patient);

        Operation operation = makeOperation();
        operation.setHealthRecord(healthRecord);
        operationRepository.save(operation);
    }
    @When("Someone tries to delete a patient, and a patient with that lbp exists in the database")
    public void someone_tries_to_delete_a_patient_and_a_patient_with_that_lbp_exists_in_the_database() throws Exception{
        Patient patient = patientRepository.findByJmbg(jmbg).get();
        assertNotNull(patient);

        resultAction = mvc.perform(delete(String.format("/patient/delete/%s", patient.getLbp()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Transactional
    @Then("Patient and all of it's data are soft deleted from the database")
    public void patient_and_all_of_it_s_data_are_soft_deleted_from_the_database() throws Exception{
        Patient patient = patientRepository.findByJmbg(jmbg).get();
        assertNotNull(patient);

        resultAction.andExpect(status().isOk())
                .andExpect(jsonPath("$.lbp").value(patient.getLbp().toString()))
                .andExpect(jsonPath("$.deleted").value(true));

        HealthRecord healthRecord = patient.getHealthRecord();

        List<Operation> operations = healthRecord.getOperations();
        if(operations != null) {
            for (Operation operation : operations) {
                assertTrue(operation.getDeleted());
            }

        }
        List<Allergy> allergies = healthRecord.getAllergies();
        if(allergies != null) {
            for (Allergy allergy : allergies) {
                assertTrue(allergy.getDeleted());
            }
        }

        List<Vaccination> vaccinations = healthRecord.getVaccinations();
        if(vaccinations != null) {
            for (Vaccination vaccination : vaccinations) {
                assertTrue(vaccination.getDeleted());
            }
        }

        List<MedicalExamination> examinations = healthRecord.getMedicalExaminations();
        if(examinations != null) {
            for (MedicalExamination examination : examinations) {
                assertTrue(examination.getDeleted());
            }
        }

        List<MedicalHistory> historyList = healthRecord.getMedicalHistory();
        if(historyList != null) {
            for (MedicalHistory history : historyList) {
                assertTrue(history.getDeleted());
            }
        }
    }



    @When("Someone tries to delete a patient, but a patient with that lbp doesn't exist in the database")
    public void someone_tries_to_delete_a_patient_but_a_patient_with_that_lbp_doesn_t_exist_in_the_database() throws Exception{
        resultAction = mvc.perform(delete(String.format("/patient/delete/%s", UUID.randomUUID()))
                .header("Authorization", "Bearer " + util.generateToken()));
    }
    @Then("BadRequestException is thrown with status code {int} saying the patient with that lbp doesn't exist and couldn't be deleted")
    public void bad_request_exception_is_thrown_with_status_code_saying_the_patient_with_that_lbp_doesn_t_exist_and_couldn_t_be_deleted(Integer statusCode) throws Exception{
        resultAction.andExpect(status().is(statusCode));
    }



    private Patient makePatient(){
        Patient patient = new Patient();

        patient.setJmbg(jmbg);
        patient.setFirstName("Test");
        patient.setLastName("Testovic");
        patient.setParentName("TestRoditelj");
        patient.setGender(Gender.MUSKI);
        patient.setBirthDate(new Date());
        patient.setBirthplace("TestMesto");
        patient.setCitizenshipCountry(CountryCode.SRB);
        patient.setCountryOfLiving(CountryCode.SRB);
        patient.setLbp(UUID.randomUUID());

        return patient;
    }

    private Operation makeOperation(){
        Operation operation = new Operation();

        operation.setDescription("TestDesc");
        operation.setDate(new Date());
        operation.setPbo(UUID.randomUUID());

        return operation;
    }
}
