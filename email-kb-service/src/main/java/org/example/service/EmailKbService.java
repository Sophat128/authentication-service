package org.example.service;

import jakarta.mail.MessagingException;
import org.example.model.Email;
import org.example.model.SmtpDto;
import org.example.model.request.SmtpRequest;

import java.io.IOException;
import java.security.Principal;

public interface EmailKbService {

    void sendConfirmationEmail(Email email) throws MessagingException, IOException, jakarta.mail.MessagingException;

    SmtpDto configEmail(SmtpRequest smtpRequest, Principal principal);

    String updateConfigEmail(Long id, SmtpRequest smtpRequest,Principal principal);
}
