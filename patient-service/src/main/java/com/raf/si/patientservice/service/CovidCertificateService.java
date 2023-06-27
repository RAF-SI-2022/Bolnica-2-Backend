package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.response.CovidCertificateResponse;
import com.raf.si.patientservice.model.Testing;
import com.raf.si.patientservice.model.VaccinationCovid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CovidCertificateService {

    CovidCertificateResponse createCertificate(VaccinationCovid vaccinationCovid);

    CovidCertificateResponse createCertificate(Testing testing);

    List<CovidCertificateResponse> getCovidCertificateHistory(UUID lbp, LocalDateTime apply, LocalDateTime end);
}
