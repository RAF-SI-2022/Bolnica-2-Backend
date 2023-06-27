package com.raf.si.patientservice.service.impl;

import com.raf.si.patientservice.dto.response.CovidCertificateResponse;
import com.raf.si.patientservice.mapper.CovidCertificateMapper;
import com.raf.si.patientservice.model.CovidCertificate;
import com.raf.si.patientservice.model.Testing;
import com.raf.si.patientservice.model.VaccinationCovid;
import com.raf.si.patientservice.repository.CovidCertificateRepository;
import com.raf.si.patientservice.repository.filtering.filter.CovidCertificateFilter;
import com.raf.si.patientservice.repository.filtering.specification.CovidCertificateSpecification;
import com.raf.si.patientservice.service.CovidCertificateService;
import com.raf.si.patientservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CovidCertificateServiceImpl implements CovidCertificateService {

    private final CovidCertificateRepository certificateRepository;
    private final CovidCertificateMapper mapper;
    private final EmailService emailService;

    public CovidCertificateServiceImpl(CovidCertificateRepository certificateRepository,
                                       CovidCertificateMapper mapper, EmailService emailService) {
        this.certificateRepository = certificateRepository;
        this.mapper = mapper;
        this.emailService = emailService;
    }

    @Override
    public CovidCertificateResponse createCertificate(VaccinationCovid vaccinationCovid) {
        CovidCertificate covidCertificate = certificateRepository.save(mapper.vaccinationCovidToCovidCertificate(vaccinationCovid));
        log.info("Napravljen kovid sertifikat");
        emailService.sendCertificate(covidCertificate, covidCertificate.getVaccinationCovid().getHealthRecord().getPatient());
        return mapper.modelToResponse(covidCertificate);
    }

    @Override
    public CovidCertificateResponse createCertificate(Testing testing) {
        CovidCertificate covidCertificate = certificateRepository.save(mapper.testingToCovidCertificate(testing));
        log.info("Napravljen kovid sertifikat");
        emailService.sendCertificate(covidCertificate, covidCertificate.getTesting().getPatient());
        return mapper.modelToResponse(covidCertificate);
    }

    @Override
    public List<CovidCertificateResponse> getCovidCertificateHistory(UUID lbp, LocalDateTime apply, LocalDateTime end) {
        CovidCertificateFilter filter = new CovidCertificateFilter(lbp, apply, end);
        CovidCertificateSpecification specification = new CovidCertificateSpecification(filter);
        log.info("Dohvatanje istorije kovid sertifikata za pacijenta sa lbp-om '{}'", lbp);
        return certificateRepository.findAll(specification)
                .stream()
                .map(mapper::modelToResponse)
                .collect(Collectors.toList());
    }
}
