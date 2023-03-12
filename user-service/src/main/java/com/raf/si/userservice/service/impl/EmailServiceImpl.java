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

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void resetPassword(String email, UUID password) {

        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        String subject = "Reset your Balkan Medic password";
        String text = "Hi,<br><br>here is your new generated password: <b>{password}</b><br><br>Thank you,<br><i>The Balkan Medic Team</i>";
        text = text.replace("{password}", password.toString());

        try {
            helper.setFrom("noreply@balkan-medic.com");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (Throwable e) {
            log.error("Error happened when sending email to '{}'", email);
            throw new InternalServerErrorException("Failed to send email");
        }

        emailSender.send(message);
    }
}
