package com.raf.si.laboratoryservice.configuration;

import com.raf.si.laboratoryservice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(SECRET_KEY);
    }

}
