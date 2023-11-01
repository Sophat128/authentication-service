package com.example.services.impl;


import com.example.dto.SmtpDto;
import com.example.exception.NotFoundException;
import com.example.models.Email;
import com.example.models.EmailConfig;
import com.example.response.ApiResponse;
import com.example.services.EmailSenderService;
import com.example.services.MailSenderFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
//import org.webjars.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.UUID;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private static final Logger LOGGER = LogManager.getLogger(EmailSenderServiceImpl.class);

    private final MailSenderFactory mailSenderFactory;

//    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final WebClient webClient;

    @Autowired
    public EmailSenderServiceImpl(MailSenderFactory mailSenderFactory, SpringTemplateEngine templateEngine, WebClient webClient) {
        this.mailSenderFactory = mailSenderFactory;
//        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.webClient = webClient;
    }
    public SmtpDto getSmtpById(UUID smtpId,UUID appId, Jwt jwt) {
        try {
            return webClient.get()
                    .uri("/smtp/{smtpId}/{appId}", smtpId,appId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(jwt.getTokenValue()))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<SmtpDto>>() {})
                    .block()
                    .getPayload();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Application not found."); // or handle it in a way you prefer
            } else {
                throw e;
            }
        }
    }


    @Override
    public void sendConfirmationEmail(Email email, UUID smtpId,UUID appId, Jwt jwt ) throws MessagingException {
        LOGGER.log(Level.INFO, () -> String.format("» sendConfirmationEmail(%s)", email.getTo()));
        SmtpDto smtpDto = getSmtpById(smtpId,appId, jwt);
        JavaMailSender mailSender = mailSenderFactory.getSender(smtpDto.getUsername(), smtpDto.getPassword());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(email.getProps());
//        String html = templateEngine.process("confirmation", context);
        helper.setTo(email.getTo());
        helper.setText(email.getContent());
        helper.setSubject(email.getSubject());
        helper.setFrom(email.getFrom());
        mailSender.send(message);
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message,
//                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//                StandardCharsets.UTF_8.name());
//
//        Context context = new Context();
//        context.setVariables(email.getProps());
//        String html = templateEngine.process("confirmation", context);
//        helper.setTo(email.getTo());
//        helper.setText(html, true);
//        helper.setSubject(email.getSubject());
//        helper.setFrom(email.getFrom());
//        javaMailSender.send(message);
    }

    @Override
    public EmailConfig cofigMailSender(UUID smtpId, UUID appId, Principal principal, Jwt jwt) {
        if (principal.getName()==null){
            throw new EntityNotFoundException("need token");
        }
        SmtpDto smtpDto = getSmtpById(smtpId,appId,jwt);
        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setUsername(smtpDto.getUsername());
        emailConfig.setPassword(smtpDto.getPassword());
        return emailConfig;
    }

//    @Override
//    public JavaMailSenderImpl setSender(UUID smtpId, UUID appId, Principal principal, Jwt jwt) {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        mailSender.setJavaMailProperties(mailProperties());
//        SmtpDto smtpDto = getSmtpById(smtpId,appId,jwt);
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setUsername(smtpDto.getUsername());
//        mailSender.setPassword(smtpDto.getPassword());
//        mailSender.setPort(587);
//        return mailSender;
////        return null;
//    }
//    private Properties mailProperties() {
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.port",587);
//        return props;
//    }
}

