package com.raf.si.patientservice.service;

import com.raf.si.patientservice.dto.request.DischargeRequest;
import com.raf.si.patientservice.dto.request.HospitalizationRequest;
import com.raf.si.patientservice.dto.request.MedicalReportRequest;
import com.raf.si.patientservice.dto.request.PatientConditionRequest;
import com.raf.si.patientservice.dto.response.*;
import com.raf.si.patientservice.model.Patient;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.UUID;

public interface HospitalizationService {
    HospitalizationResponse hospitalize(HospitalizationRequest request, String token);

    HospitalisedPatientsListResponse getHospitalisedPatients(String token, UUID pbo, UUID lbp,
                                                             String firstName, String lastName, String jmbg,
                                                             String covid, Pageable pageable);

    HospPatientByHospitalListResponse getHospitalisedPatientsByHospital(String token, UUID pbb, UUID lbp,
                                                                        String firstName, String lastName, String jmbg,
                                                                        String respirator, String imunizovan, String covid,
                                                                        Pageable pageable);

    PatientConditionResponse createPatientCondition(UUID lbp, PatientConditionRequest patientConditionRequest);

    PatientConditionListResponse getPatientConditions(UUID lbp, Date dateFrom, Date dateTo, Pageable pageable);

    Patient getHospitalisedPatientByLbp(UUID lbp);

    MedicalReportResponse createMedicalReport(UUID lbp, MedicalReportRequest request);

    MedicalReportListResponse getMedicalReports(UUID lbp, Date from, Date to, String covid, Pageable pageable);

    DischargeResponse createDischarge(UUID lbp, DischargeRequest request, String token);

    DischargeListResponse getDischarge(UUID lbp, Date dateFrom, Date dateTo, String covid, Pageable pageable, String token);
}
