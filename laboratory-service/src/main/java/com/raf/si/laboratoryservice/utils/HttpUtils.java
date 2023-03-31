package com.raf.si.laboratoryservice.utils;

import com.raf.si.laboratoryservice.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class HttpUtils {

    @Value("${user-service-url}")
    private static String USER_SERVICE_BASE_URL;


    public static ResponseEntity<UserResponse> findUserByLbz(String token, UUID lbz){
        String url = USER_SERVICE_BASE_URL + "/" + lbz;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<?> entity = new HttpEntity<>(null, headers);
        ResponseEntity<UserResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserResponse.class);
        return response;
    }
}
