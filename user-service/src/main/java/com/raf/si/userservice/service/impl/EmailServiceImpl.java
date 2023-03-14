package com.raf.si.userservice.service.impl;

import com.raf.si.userservice.exception.InternalServerErrorException;
import com.raf.si.userservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    public final String uri = "http://localhost:4200/forgot-password/";

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void resetPassword(String email, UUID passwordToken) {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String subject = "Resetuj svoju Balkan Medic sifru";
        String text = "Postovani,<br><br>da biste resetovali sifru kliknite na sledeci link: <b><a href=\"{sendAddress}\">" +
                "Resetuj sifru</a></b><br><br>Thank you,<br><i>The Balkan Medic Team</i>";
        String resetAddress = uri + passwordToken.toString();
        text = text.replace("{sendAddress}", resetAddress);

        try {
            helper.setFrom("balkan.medic2023@outlook.com");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
            emailSender.send(message);
        } catch (Throwable e) {
            log.error("Error happened when sending email to '{}'", email, e);
            throw new InternalServerErrorException("Failed to send email");
        }
        log.info("Imejl uspesno poslat na adresu '{}'", email);
    }
}
