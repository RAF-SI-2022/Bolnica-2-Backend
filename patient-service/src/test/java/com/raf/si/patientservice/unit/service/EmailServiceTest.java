package com.raf.si.patientservice.unit.service;

import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.model.enums.certificate.CovidCertificateType;
import com.raf.si.patientservice.model.enums.patient.Gender;
import com.raf.si.patientservice.model.enums.testing.TestResult;
import com.raf.si.patientservice.service.EmailService;
import com.raf.si.patientservice.service.impl.EmailServiceImpl;
import com.raf.si.patientservice.utils.PDFUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    private EmailService emailService;
    private JavaMailSender javaMailSender;

    @BeforeEach
    public void setUp() throws InterruptedException {
        javaMailSender = mock(JavaMailSender.class);
        emailService = new EmailServiceImpl(javaMailSender);
        try (MockedStatic<PDFUtil> utilities = mockStatic(PDFUtil.class)) {
            utilities.when(() -> PDFUtil.createPDF(any(), any()))
                    .thenReturn(null);
        }
    }

    @Test
    public void sendCertificate_JavaMailSenderThrowsMailSendException_ThrowsInternalServerError() {
        MimeMessage message = new MimeMessage((Session) null);
        Patient patient = makePatient();
        VaccinationCovid vaccinationCovid = makeVaccinationCovid();
        CovidCertificate covidCertificate = makeCovidCertificate();
        covidCertificate.setVaccinationCovid(vaccinationCovid);
        covidCertificate.setCovidCertificateType(CovidCertificateType.PRIMLJENA_VAKCINA);

        when(javaMailSender.createMimeMessage()).thenReturn(message);
        doThrow(new MailSendException("error")).when(javaMailSender).send(any(MimeMessage.class));

        assertThrows(InternalServerErrorException.class,
                () -> emailService.sendCertificate(covidCertificate, patient));
    }

    @Test
    public void sendCertificate_WhenVaccination_Success() {
        MimeMessage message = new MimeMessage((Session) null);
        Patient patient = makePatient();
        VaccinationCovid vaccinationCovid = makeVaccinationCovid();
        CovidCertificate covidCertificate = makeCovidCertificate();
        covidCertificate.setVaccinationCovid(vaccinationCovid);
        covidCertificate.setCovidCertificateType(CovidCertificateType.PRIMLJENA_VAKCINA);

        when(javaMailSender.createMimeMessage()).thenReturn(message);

        emailService.sendCertificate(covidCertificate, patient);
    }

    @Test
    public void sendCertificate_WhenTesting_Success() {
        MimeMessage message = new MimeMessage((Session) null);
        Patient patient = makePatient();
        Testing testing = makeTesting();
        CovidCertificate covidCertificate = makeCovidCertificate();
        covidCertificate.setTesting(testing);
        covidCertificate.setCovidCertificateType(CovidCertificateType.OPORAVAK_OD_COVIDA);

        when(javaMailSender.createMimeMessage()).thenReturn(message);

        emailService.sendCertificate(covidCertificate, patient);
    }

    private Patient makePatient() {
        Patient patient = new Patient();
        patient.setBirthDate(new Date());
        patient.setJmbg("31231321321");
        patient.setEmail("email");
        patient.setFirstName("first name");
        patient.setLastName("last name");
        patient.setGender(Gender.MUSKI);

        return patient;
    }

    private CovidCertificate makeCovidCertificate() {
        CovidCertificate covidCertificate = new CovidCertificate();
        covidCertificate.setCertificateNumber(UUID.randomUUID());
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
        return vaccinationCovid;
    }

    private Testing makeTesting() {
        Testing testing = new Testing();
        testing.setTestResult(TestResult.POZITIVAN);
        return testing;
    }


}
