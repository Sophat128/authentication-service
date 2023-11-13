package org.example.service.serviceImpl;

import org.example.service.MailSenderFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class MailSenderFactoryImpl implements MailSenderFactory {
    @Override
    public JavaMailSender getSender(String email, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(mailProperties());
        mailSender.setHost("smtp.gmail.com");
        mailSender.setUsername(email);
        mailSender.setPassword(password);
        mailSender.setPort(587);
        return mailSender;
    }

    private Properties mailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port",587);
        return props;
    }
}
