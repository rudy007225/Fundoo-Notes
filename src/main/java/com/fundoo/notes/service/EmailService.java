package com.fundoo.notes.service;

public interface EmailService {

    void sendPasswordResetEmail(String toEmail, String resetLink);
}
