package com.raf.si.patientservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${mail.host}")
    private String mailHost;
    @Value("${mail.port}")
    private Integer mailPort;
    @Value("${mail.username}")
    private String mailUsername;
    @Value("${mail.password}")
    private String mailPassword;
    @Value("${mail.smtp.host}")
    private String smtpHost;
    @Value("${mail.smtp.starttls.enable}")
    private String startTls;
    @Value("${mail.smtp.auth}")
    private String smtpAuth;
    @Value("${mail.smtp.debug}")
    private String debug;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", mailPort.toString());
        props.put("mail.smtp.starttls.enable", startTls);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.debug", debug);

        return mailSender;
    }
}
