package com.raf.si.userservice.utils;

import com.raf.si.userservice.dto.request.TimeRequest;
import com.raf.si.userservice.dto.request.UpdateTermsNewShiftRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class HttpUtils {

    @Value("${patient-service-url}")
    private static String PATIENT_SERVICE_BASE_URL;

    private static final String PATIENT_SCHED_MED_EXAM = "/sched-med-exam";
    private static final String PATIENT_DOCTOR_HAS_SCHEDULED_EXAMS_IN_TIMESLOT = "/has-for-timeslot";
    private static final String PATIENT_UPDATE_NURSE_AVAILABLE_TERMS = "/testing/update-nurse-terms";

    public static List<Date> checkDoctorScheduledExamsForTimeSlot(UUID lbz, UpdateTermsNewShiftRequest request, String token) {
        String url = PATIENT_SERVICE_BASE_URL + PATIENT_SCHED_MED_EXAM + "/" + lbz + PATIENT_DOCTOR_HAS_SCHEDULED_EXAMS_IN_TIMESLOT;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<?> entity = new HttpEntity<>(request, headers);
        ResponseEntity<Date[]> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Date[].class
        );

        if (response.getBody() == null) {
            return null;
        }

        return Arrays.asList(response.getBody());
    }

    public static List<LocalDateTime> checkAndUpdateNurseTerms(UpdateTermsNewShiftRequest request, String token) {
        String url = PATIENT_SERVICE_BASE_URL + PATIENT_UPDATE_NURSE_AVAILABLE_TERMS;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<?> entity = new HttpEntity<>(request, headers);
        ResponseEntity<LocalDateTime[]> response =  restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                LocalDateTime[].class
        );

        if (response.getBody() == null) {
            return null;
        }

        return Arrays.asList(response.getBody());
    }

    @Autowired
    private void setStaticVariables(@Value("${patient-service-url}") String patientServiceUrl){
        HttpUtils.PATIENT_SERVICE_BASE_URL = patientServiceUrl;
    }

}
