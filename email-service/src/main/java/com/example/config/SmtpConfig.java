package com.example.config;

import com.example.models.EmailConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class SmtpConfig {

//    private EmailConfig emailConfig;
//    @Bean
//    public EmailConfig emailConfig(){
//        return new EmailConfig();
//    }
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//        mailSender.setUsername(emailConfig().getUsername());
//        mailSender.setPassword(emailConfig().getPassword());
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        System.out.println(mailSender.getUsername());
//        System.out.println(mailSender.getPassword());
//        return mailSender;
//    }
}
