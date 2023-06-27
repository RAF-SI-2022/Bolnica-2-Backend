package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.model.CovidCertificate;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.service.EmailService;
import com.raf.si.patientservice.utils.PDFUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendCertificate(CovidCertificate covidCertificate, Patient patient) {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper;

        String subject = "Kovid sertifikat";
        String text = "Postovani,<br><br>u prilogu Vam šaljemo kovid sertifikat u PDF formatu.><br><br>Hvala,<br><i>The Balkan Medic Team</i>";
        File file = null;
        try {
            helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom("balkan.medic2023@outlook.com");
            helper.setTo(patient.getEmail());
            helper.setSubject(subject);
            helper.setText(text, true);
            file = PDFUtil.createPDF(covidCertificate, patient);
            helper.addAttachment(file.getName(), file);
            emailSender.send(message);
        } catch (Throwable e) {
            log.error("Error happened when sending email to '{}'", patient.getEmail(), e);
            throw new InternalServerErrorException("Failed to send email");
        } finally {
            if(file != null) {
                try {
                    FileUtils.delete(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("Imejl uspesno poslat na adresu '{}'", patient.getEmail());
    }
}
