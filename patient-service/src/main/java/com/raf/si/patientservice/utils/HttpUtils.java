package com.raf.si.patientservice.utils;

import com.raf.si.patientservice.dto.request.UUIDListRequest;
import com.raf.si.patientservice.dto.response.http.DepartmentResponse;
import com.raf.si.patientservice.dto.response.http.DoctorResponse;
import com.raf.si.patientservice.dto.response.http.ReferralResponse;
import com.raf.si.patientservice.dto.response.http.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class HttpUtils {

    private static String USER_SERVICE_BASE_URL;
    private static String LABORATORY_SERVICE_BASE_URL;

    private static final String USER_GET_USER_INFO = "/users/employee-info";
    private static final String USER_HEAD_OF_DEPARTMENT = "/users/head-department";
    private static final String USER_DEPARTMENT = "/departments";
    private static final String CHANGE_REFERRAL_STATUS_URL = "/referral/change-status";
    private static final String USER_DOCTORS = "/users/doctors";
    private static final String USER_COVID_NURSES_FOR_DEPARTMENT = "/users/covid-nurses-num";


    public static ResponseEntity<UserResponse> findUserByLbz(String token, UUID lbz) {
        String url = USER_SERVICE_BASE_URL + USER_GET_USER_INFO + "/" + lbz;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                url.trim(),
                HttpMethod.GET,
                entity,
                UserResponse.class
        );
    }

    public static List<UserResponse> findUsersByLbzList(UUIDListRequest lbzListRequest, String token) {
        String url = USER_SERVICE_BASE_URL + "/users/lbz/list";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<UUIDListRequest> entity = new HttpEntity<>(lbzListRequest, headers);
        ResponseEntity<UserResponse[]> response = restTemplate.exchange(
                url.trim(),
                HttpMethod.POST,
                entity,
                UserResponse[].class
        );
        return Arrays.asList(response.getBody());
    }

    public static ResponseEntity<DepartmentResponse> findDepartmentByPbo(UUID pbo, String token) {
        String url = USER_SERVICE_BASE_URL + USER_DEPARTMENT + "/pbo/" + pbo;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url.trim(),
                HttpMethod.GET,
                entity,
                DepartmentResponse.class
        );
    }

    public static ResponseEntity<ReferralResponse> changeReferralStatus(Long id, String status, String token) {
        String url = LABORATORY_SERVICE_BASE_URL + CHANGE_REFERRAL_STATUS_URL + "/" + id + "/?status=" + status;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                url.trim(),
                HttpMethod.PUT,
                entity,
                ReferralResponse.class
        );
    }

    public static ResponseEntity<DoctorResponse[]> findDoctors(String token) {
        String url = USER_SERVICE_BASE_URL + USER_DOCTORS;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                url.trim(),
                HttpMethod.GET,
                entity,
                DoctorResponse[].class
        );
    }

    public static ResponseEntity<DepartmentResponse[]> findDepartmentsByHospital(UUID pbb, String token) {
        String url = USER_SERVICE_BASE_URL + USER_DEPARTMENT + "/" + pbb;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                url.trim(),
                HttpMethod.GET,
                entity,
                DepartmentResponse[].class
        );
    }

    public static ResponseEntity<DoctorResponse> getHeadOfDepartment(UUID pbo, String token) {
        String url = USER_SERVICE_BASE_URL + USER_HEAD_OF_DEPARTMENT + "/" + pbo;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url.trim(),
                HttpMethod.GET,
                entity,
                DoctorResponse.class
        );
    }

    public static Integer getNumOfCovidNursesForDepartment(UUID pbo, String token) {
        String url = USER_SERVICE_BASE_URL + USER_COVID_NURSES_FOR_DEPARTMENT + "/" + pbo;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url.trim(),
                HttpMethod.GET,
                entity,
                Integer.class
        ).getBody();
    }

    @Autowired
    private void setStaticVariables(@Value("${user-service-url}") String userServiceUrl,
                                    @Value("${laboratory-service-url}") String labServiceUrl) {

        HttpUtils.USER_SERVICE_BASE_URL = userServiceUrl;
        HttpUtils.LABORATORY_SERVICE_BASE_URL = labServiceUrl;
    }
}
