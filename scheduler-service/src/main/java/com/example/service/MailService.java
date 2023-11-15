package com.example.service;

public interface MailService {

    public void sendMail(String fromEmail, String toEmail, String subject, String message);

}
