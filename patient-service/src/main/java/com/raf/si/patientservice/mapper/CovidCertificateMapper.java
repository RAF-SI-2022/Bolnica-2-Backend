package com.raf.si.patientservice.mapper;

import com.raf.si.patientservice.dto.response.CovidCertificateResponse;
import com.raf.si.patientservice.model.CovidCertificate;
import com.raf.si.patientservice.model.Testing;
import com.raf.si.patientservice.model.VaccinationCovid;
import com.raf.si.patientservice.model.enums.certificate.CovidCertificateType;
import com.raf.si.patientservice.model.enums.testing.TestResult;
import org.springframework.stereotype.Component;

@Component
public class CovidCertificateMapper {

    public CovidCertificate vaccinationCovidToCovidCertificate(VaccinationCovid vaccinationCovid) {
        CovidCertificate covidCertificate = new CovidCertificate();
        covidCertificate.setCovidCertificateType(CovidCertificateType.PRIMLJENA_VAKCINA);
        covidCertificate.setVaccinationCovid(vaccinationCovid);
        covidCertificate.setDateApply(vaccinationCovid.getDateTime());
        int dose = Math.toIntExact(vaccinationCovid.getDoseReceived());
        if (dose == 2) {
            covidCertificate.setEndDate(covidCertificate.getDateApply().plusDays(210));
        } else {
            covidCertificate.setEndDate(covidCertificate.getDateApply());
        }
        return covidCertificate;
    }

    public CovidCertificate testingToCovidCertificate(Testing testing) {
        CovidCertificate covidCertificate = new CovidCertificate();
        if (testing.getTestResult() == TestResult.POZITIVAN) {
            covidCertificate.setCovidCertificateType(CovidCertificateType.OPORAVAK_OD_COVIDA);
            covidCertificate.setDateApply(testing.getDateAndTime().plusDays(11));
            covidCertificate.setEndDate(covidCertificate.getDateApply().plusDays(180));
        } else {
            covidCertificate.setCovidCertificateType(CovidCertificateType.NEGATIVAN_PCR_TEST);
            covidCertificate.setDateApply(testing.getDateAndTime());
            covidCertificate.setEndDate(testing.getDateAndTime().plusDays(3));
        }
        covidCertificate.setTesting(testing);

        return covidCertificate;
    }

    public CovidCertificateResponse modelToResponse(CovidCertificate covidCertificate) {
        CovidCertificateResponse response = new CovidCertificateResponse();
        response.setId(covidCertificate.getId());
        response.setCertificateNumber(covidCertificate.getCertificateNumber());
        response.setCovidCertificateType(covidCertificate.getCovidCertificateType());
        response.setDateApply(covidCertificate.getDateApply());
        response.setDateOfIssue(covidCertificate.getDateOfIssue());
        response.setEndDate(covidCertificate.getEndDate());
        if (covidCertificate.getTesting() != null) {
            response.setTestingId(covidCertificate.getTesting().getId());
        } else {
            response.setVaccinationCovidId(covidCertificate.getVaccinationCovid().getId());
        }
        return response;
    }
}
