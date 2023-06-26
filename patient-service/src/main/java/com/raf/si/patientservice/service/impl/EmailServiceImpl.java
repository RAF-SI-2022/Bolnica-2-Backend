package com.raf.si.patientservice.service.impl;

import com.aspose.pdf.Document;
import com.aspose.pdf.TextFragment;
import com.aspose.pdf.TextFragmentAbsorber;
import com.aspose.pdf.TextFragmentCollection;
import com.raf.si.patientservice.dto.CertificatePlaceHolders;
import com.raf.si.patientservice.exception.InternalServerErrorException;
import com.raf.si.patientservice.model.CovidCertificate;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.enums.certificate.CovidCertificateType;
import com.raf.si.patientservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private static final String VACCINATION_PDF = "certificate/Certificate_vaccination.pdf";
    private static final String TESTING_PDF = "certificate/Certificate_testing.pdf";
    private static final String DEST_FILE = "patient-service/src/main/resources/certificate/";

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
            file = createPDF(covidCertificate, patient);
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

    private File createPDF(CovidCertificate covidCertificate, Patient patient) {
        Resource resource;
        if(covidCertificate.getCovidCertificateType() == CovidCertificateType.PRIMLJENA_VAKCINA){
            resource = new ClassPathResource(VACCINATION_PDF);
        } else {
            resource = new ClassPathResource(TESTING_PDF);
        }

        Document pdfDocument = null;
        String filename = UUID.randomUUID().toString() + ".pdf";
        Map<String, String> words = new HashMap<>();
        addWordsToReplace(words, covidCertificate, patient);
        File file = new File(DEST_FILE + filename);
        FileOutputStream outputStream;
        try {
            outputStream = FileUtils.openOutputStream(file);
            pdfDocument = new Document(resource.getInputStream());
            for(Map.Entry<String, String> entry: words.entrySet()) {

                // Create TextAbsorber object to find all instances of the input search phrase
                TextFragmentAbsorber textFragmentAbsorber = new TextFragmentAbsorber(entry.getKey());

                // Accept the absorber for first page of document
                pdfDocument.getPages().accept(textFragmentAbsorber);

                // Get the extracted text fragments into collection
                TextFragmentCollection textFragmentCollection = textFragmentAbsorber.getTextFragments();

                // Loop through the fragments
                for (TextFragment textFragment : textFragmentCollection) {
                    // Update text and other properties
                    textFragment.setText(entry.getValue());

                }
            }
            // Save the updated PDF file
            pdfDocument.save(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(pdfDocument != null)
                pdfDocument.close();
        }
        return file;
    }

    private void addWordsToReplace(Map<String, String> words, CovidCertificate covidCertificate, Patient patient) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        words.put(CertificatePlaceHolders.CERTIFICATE_NUMBER, covidCertificate.getCertificateNumber().toString());
        words.put(CertificatePlaceHolders.FIRST_NAME,patient.getFirstName());
        words.put(CertificatePlaceHolders.LAST_NAME, patient.getLastName());
        words.put(CertificatePlaceHolders.DATE_OF_BIRTH, sdf.format(patient.getBirthDate()));
        words.put(CertificatePlaceHolders.GENDER, patient.getGender().getNotation());
        words.put(CertificatePlaceHolders.JMBG, patient.getJmbg());
        words.put(CertificatePlaceHolders.DATE_OF_ISSUE,
                sdf.format(Date.from(covidCertificate.getDateOfIssue().atZone(ZoneId.systemDefault()).toInstant())));

        if(covidCertificate.getVaccinationCovid() != null) {
            words.put(CertificatePlaceHolders.VACCINE_NAME, covidCertificate.getVaccinationCovid().getVaccine().getName());
            words.put(CertificatePlaceHolders.DOSE,covidCertificate.getVaccinationCovid().getDoseReceived().toString());
        }

        if(covidCertificate.getTesting() != null) {
            words.put(CertificatePlaceHolders.RESULT, covidCertificate.getTesting().getTestResult().getNotation());
        }

        words.put(CertificatePlaceHolders.TYPE, covidCertificate.getCovidCertificateType().getNotation());
        words.put(CertificatePlaceHolders.DATE_APPLY,
                sdf.format(Date.from(covidCertificate.getDateApply().atZone(ZoneId.systemDefault()).toInstant())));
        words.put(CertificatePlaceHolders.DATE_END,
                sdf.format(Date.from(covidCertificate.getEndDate().atZone(ZoneId.systemDefault()).toInstant())));
    }
}
