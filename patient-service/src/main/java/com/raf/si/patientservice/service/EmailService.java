package com.raf.si.patientservice.service;

import com.raf.si.patientservice.model.CovidCertificate;
import com.raf.si.patientservice.model.Patient;

public interface EmailService {

    void sendCertificate(CovidCertificate covidCertificate, Patient patient);
}
