package com.raf.si.patientservice.configuration;

import com.raf.si.patientservice.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(SECRET_KEY);
    }

    @Bean
    public DefaultLockRepository DefaultLockRepository(DataSource dataSource){
        return new DefaultLockRepository(dataSource);
    }

    @Bean
    public JdbcLockRegistry jdbcLockRegistry(LockRepository lockRepository){
        return new JdbcLockRegistry(lockRepository);
    }
}
