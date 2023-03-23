package com.raf.si.patientservice.utils;

import com.raf.si.patientservice.dto.response.http.UserResponse;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

public class HttpUtils {

    @Value("${user-service-url}")
    private static String USER_SERVICE_BASE_URL;

}
