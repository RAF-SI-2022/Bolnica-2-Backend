package com.raf.si.userservice.unit;

import com.raf.si.userservice.exception.InternalServerErrorException;
import com.raf.si.userservice.service.EmailService;
import com.raf.si.userservice.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    private JavaMailSender javaMailSender;
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        javaMailSender = mock(JavaMailSender.class);
        emailService = new EmailServiceImpl(javaMailSender);
    }

    @Test
    public void resetPassword_WhenMailException_ThrowsInternaleServerErrorException() {
        String email = "email@gmail.com";
        UUID password = UUID.randomUUID();
        MimeMessage message = new MimeMessage((Session) null);

        when(javaMailSender.createMimeMessage()).thenReturn(message);
        doThrow(new MailSendException("error")).when(javaMailSender).send(any(MimeMessage.class));

        assertThrows(InternalServerErrorException.class, () -> emailService.resetPassword(email, password));

    }

    @Test
    public void resetPassword_success() {
        String email = "email@gmail.com";
        UUID password = UUID.randomUUID();
        MimeMessage message = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(message);
        emailService.resetPassword(email, password);
    }
}
