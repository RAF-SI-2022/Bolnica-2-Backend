package com.raf.si.patientservice.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.TextFragment;
import com.aspose.pdf.TextFragmentAbsorber;
import com.aspose.pdf.TextFragmentCollection;
import com.raf.si.patientservice.dto.CertificatePlaceHolders;
import com.raf.si.patientservice.model.CovidCertificate;
import com.raf.si.patientservice.model.Patient;
import com.raf.si.patientservice.model.enums.certificate.CovidCertificateType;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PDFUtil {

    private static final String VACCINATION_PDF = "certificate/Certificate_vaccination.pdf";
    private static final String TESTING_PDF = "certificate/Certificate_testing.pdf";
    private static final String DEST_FILE = "certificate/";

    public static File createPDF(CovidCertificate covidCertificate, Patient patient) {
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

    private static void addWordsToReplace(Map<String, String> words, CovidCertificate covidCertificate, Patient patient) {
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
