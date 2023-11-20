package org.example.service;

import jakarta.mail.MessagingException;
import org.example.model.Email;
import org.example.model.SmtpDto;
import org.example.model.request.SmtpRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface EmailKbService {

    void sendConfirmationEmail(Email email) throws MessagingException, IOException, jakarta.mail.MessagingException;

//    SmtpDto configEmail(SmtpRequest smtpRequest);

    String updateConfigEmail(SmtpRequest smtpRequest);

    List<SmtpDto> getConfigEmail();




}
