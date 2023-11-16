package com.example.service.impl;

import com.example.exception.InternalServerException;
import com.example.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Component
public class MailServiceImpl implements MailService {


    private final JavaMailSender javaMailSender;

    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(String fromEmail, String toEmail, String subject, String message) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
            messageHelper.setSubject(subject);
            messageHelper.setText(message, true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            throw new InternalServerException("Error Sending mail");
        }
    }
}
