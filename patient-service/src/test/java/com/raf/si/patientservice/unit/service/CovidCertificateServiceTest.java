package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.mapper.CovidCertificateMapper;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.testing.TestResult;
import com.raf.si.patientservice.repository.CovidCertificateRepository;
import com.raf.si.patientservice.repository.filtering.filter.CovidCertificateFilter;
import com.raf.si.patientservice.repository.filtering.specification.CovidCertificateSpecification;
import com.raf.si.patientservice.service.CovidCertificateService;
import com.raf.si.patientservice.service.EmailService;
import com.raf.si.patientservice.service.impl.CovidCertificateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CovidCertificateServiceTest {

    private CovidCertificateService covidCertificateService;
    private CovidCertificateMapper covidCertificateMapper;
    private EmailService emailService;
    private CovidCertificateRepository repository;

    @BeforeEach
    public void setUp() {
        emailService = mock(EmailService.class);
        covidCertificateMapper = new CovidCertificateMapper();
        repository = mock(CovidCertificateRepository.class);
        covidCertificateService = new CovidCertificateServiceImpl(repository, covidCertificateMapper,
                emailService);
    }

    @Test
    public void createCertificate_Vaccination_Success() {
        VaccinationCovid vaccinationCovid = makeVaccinationCovid();
        CovidCertificate covidCertificate = makeCovidCertificate();
        covidCertificate.setVaccinationCovid(vaccinationCovid);

        when(repository.save(any())).thenReturn(covidCertificate);

        assertEquals(covidCertificateService.createCertificate(vaccinationCovid),
                covidCertificateMapper.modelToResponse(covidCertificate));

        covidCertificate.getVaccinationCovid().setDoseReceived(2L);

        assertEquals(covidCertificateService.createCertificate(vaccinationCovid),
                covidCertificateMapper.modelToResponse(covidCertificate));
    }

    @Test
    public void createCertificate_Testing_Success() {
        Testing testing = makeTesting();
        CovidCertificate covidCertificate = makeCovidCertificate();
        covidCertificate.setTesting(testing);

        when(repository.save(any())).thenReturn(covidCertificate);

        assertEquals(covidCertificateService.createCertificate(testing),
                covidCertificateMapper.modelToResponse(covidCertificate));

        covidCertificate.getTesting().setTestResult(TestResult.NEGATIVAN);

        assertEquals(covidCertificateService.createCertificate(testing),
                covidCertificateMapper.modelToResponse(covidCertificate));
    }

    @Test
    public void getCovidCertificateHistory_Success() {
        Testing testing = makeTesting();
        CovidCertificate covidCertificate = makeCovidCertificate();
        covidCertificate.setTesting(testing);

        UUID lbp = UUID.randomUUID();
        LocalDateTime localDateTime = LocalDateTime.now();

        CovidCertificateFilter filter = new CovidCertificateFilter(lbp, localDateTime, localDateTime);
        CovidCertificateSpecification specification = new CovidCertificateSpecification(filter);
        List<CovidCertificate> covidCertificates = Collections.singletonList(covidCertificate);

        when(repository.findAll(any(CovidCertificateSpecification.class))).thenReturn(covidCertificates);

        assertEquals(covidCertificateService.getCovidCertificateHistory(lbp, localDateTime, localDateTime),
                covidCertificates.stream().map(covidCertificateMapper::modelToResponse).collect(Collectors.toList()));
    }

    private CovidCertificate makeCovidCertificate() {
        CovidCertificate covidCertificate = new CovidCertificate();
        covidCertificate.setCertificateNumber(UUID.fromString("588a0b44-6f5d-4097-92eb-8c5228fd47d2"));
        covidCertificate.setDateOfIssue(LocalDateTime.now());
        covidCertificate.setDateApply(LocalDateTime.now());
        covidCertificate.setEndDate(LocalDateTime.now());

        return covidCertificate;
    }

    private VaccinationCovid makeVaccinationCovid() {
        VaccinationCovid vaccinationCovid = new VaccinationCovid();
        Vaccine vaccine = new Vaccine();
        vaccine.setName("vaccineName");
        vaccinationCovid.setVaccine(vaccine);
        vaccinationCovid.setDoseReceived(1L);
        vaccinationCovid.setDateTime(LocalDateTime.now());

        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setPatient(new Patient());
        vaccinationCovid.setHealthRecord(healthRecord);
        return vaccinationCovid;
    }

    private Testing makeTesting() {
        Testing testing = new Testing();
        testing.setTestResult(TestResult.POZITIVAN);
        return testing;
    }
}
