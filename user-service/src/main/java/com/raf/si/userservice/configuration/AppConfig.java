package com.raf.si.userservice.configuration;

import com.raf.si.userservice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(SECRET_KEY);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
