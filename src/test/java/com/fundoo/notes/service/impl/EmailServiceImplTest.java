package com.fundoo.notes.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendPasswordResetEmail() {
        String toEmail = "recipient@example.com";
        String resetLink = "http://localhost:8080/api/users/reset-password?token=sampleToken";

        emailService.sendPasswordResetEmail(toEmail, resetLink);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertArrayEquals(new String[]{toEmail}, capturedMessage.getTo());
        assertEquals("Reset your FundooNotes password", capturedMessage.getSubject());
        assertTrue(Objects.requireNonNull(capturedMessage.getText()).contains(resetLink));
    }
}
