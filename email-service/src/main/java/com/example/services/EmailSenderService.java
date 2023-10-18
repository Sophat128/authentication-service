package com.example.services;

import com.example.models.Email;
import org.springframework.messaging.MessagingException;

import java.io.IOException;

public interface EmailSenderService {
//    void sendEmail(Email email);
    void sendConfirmationEmail(Email email) throws MessagingException, IOException, jakarta.mail.MessagingException;
}
