package com.raf.si.laboratoryservice.utils;

import com.raf.si.laboratoryservice.dto.response.DepartmentResponse;
import com.raf.si.laboratoryservice.dto.response.DoctorResponse;
import com.raf.si.laboratoryservice.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
@Component
public class HttpUtils {

    @Value("${user-service-url}")
    private static String USER_SERVICE_BASE_URL;

    private static String USER_GET_All_DOCTORS = "/users/doctors";

    private static String USER_GET_DEPARTMENT_NAME = "/departments";


    public static ResponseEntity<List<DoctorResponse>> getAllDoctors(String token) {
        String url = USER_SERVICE_BASE_URL + USER_GET_All_DOCTORS;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<DoctorResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response;
    }

    public static ResponseEntity<List<DepartmentResponse>> findDepartmentName(String token) {
        String url = USER_SERVICE_BASE_URL + USER_GET_DEPARTMENT_NAME;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);
        ResponseEntity<List<DepartmentResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response;
    }

    @Autowired
    private void setStaticVariables(@Value("${user-service-url}") String userServiceUrl){
        HttpUtils.USER_SERVICE_BASE_URL = userServiceUrl;
    }

}
