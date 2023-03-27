package com.raf.si.patientservice.utils;

import com.raf.si.patientservice.dto.response.http.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class HttpUtils {

    private static String USER_SERVICE_BASE_URL;


    public static ResponseEntity<UserResponse> findUserByLbz(String token, UUID lbz){
        String url = USER_SERVICE_BASE_URL + "/" + lbz;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<UserResponse> response = restTemplate.exchange(url.trim(), HttpMethod.GET, entity, UserResponse.class);
        return response;
    }

    @Autowired
    private void setStaticVariables(@Value("${user-service-url}") String userServiceUrl){
        HttpUtils.USER_SERVICE_BASE_URL=userServiceUrl;
    }
}
