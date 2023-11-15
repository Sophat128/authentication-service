package org.example.service.serviceImpl;

import com.example.dto.UserDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.example.exception.BadRequestException;
import org.example.exception.ForbiddenException;
import org.example.exception.NotFoundException;
import org.example.model.Email;
import org.example.model.Smtp;
import org.example.model.SmtpDto;
import org.example.model.request.SmtpRequest;
import org.example.repository.EmailKbServiceRepository;
import org.example.service.EmailKbService;
import org.example.service.MailSenderFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.context.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class EmailServiceImpl implements EmailKbService {
    private final MailSenderFactory mailSenderFactory;
    private final EmailKbServiceRepository emailKbServiceRepository;
    private final WebClient webClient;

    public EmailServiceImpl(MailSenderFactory mailSenderFactory, EmailKbServiceRepository emailKbServiceRepository,WebClient webClient) {
        this.mailSenderFactory = mailSenderFactory;
        this.emailKbServiceRepository = emailKbServiceRepository;
        this.webClient = webClient;
    }


    public UserDto getUserById(UUID userId) {
        return webClient.get()
                .uri("/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }


    @Override
    public void sendConfirmationEmail(Email email) throws MessagingException {
        List<Smtp> smtp = emailKbServiceRepository.findAll();
        JavaMailSender mailSender = mailSenderFactory.getSender(smtp.get(0).getUsername(),smtp.get(0).getPassword());
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                true,  // true indicates multipart message
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(email.getProps());
        String[] toAddresses = email.getTo().toArray(new String[0]);
        InternetAddress[] recipientAddresses = new InternetAddress[toAddresses.length];
        for (int i = 0; i < toAddresses.length; i++) {
            recipientAddresses[i] = new InternetAddress(toAddresses[i]);
        }
        helper.setTo(recipientAddresses);
        helper.setText(email.getContent(), true);  // true indicates HTML content
        helper.setSubject(email.getSubject());
        helper.setFrom(email.getFrom());

        // Attach file
        if (email.getAttachmentFilePath() != null) {
            FileSystemResource file = new FileSystemResource(new File(email.getAttachmentFilePath()));
            helper.addAttachment(file.getFilename(), file);
        }

        mailSender.send(message);
    }




    @Override
    public SmtpDto configEmail(SmtpRequest smtpRequest, Principal principal) {
        if(principal == null){
            throw new ForbiddenException("Need Token");
        }
        UUID userId = UUID.fromString(principal.getName());
        if (smtpRequest.getUsername().isBlank() || smtpRequest.getUsername().isEmpty()) {
            throw new BadRequestException("Field username can't be blank");
        }

        if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
            throw new BadRequestException("Username should be like this -> something@something.com");
        }

        if (smtpRequest.getPassword().isBlank() || smtpRequest.getPassword().isEmpty()) {
            throw new BadRequestException("Field password can't be blank");
        }
        Smtp smtp = new Smtp();
        smtp.setUsername(smtpRequest.getUsername());
        smtp.setPassword(smtpRequest.getPassword());
        smtp.setUserId(userId);
        emailKbServiceRepository.save(smtp) ;
        SmtpDto smtpDto = new SmtpDto();
        UserDto userById =getUserById(userId);
        smtpDto.setId(smtp.getId());
        smtpDto.setUsername(smtp.getUsername());
        smtpDto.setUserDto(userById);
        return smtpDto;
    }

    @Override
    public String updateConfigEmail(Long id, SmtpRequest smtpRequest,Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        Smtp smtp = emailKbServiceRepository.findIdandUserId(id,userId);
        if (smtp != null){
            if (smtpRequest.getUsername().isBlank() || smtpRequest.getUsername().isEmpty()) {
                throw new BadRequestException("Field username can't be blank");
            }

            if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
                throw new BadRequestException("Username should be like this -> something@something.com");
            }

            if (smtpRequest.getPassword().isBlank() || smtpRequest.getPassword().isEmpty()) {
                throw new BadRequestException("Field password can't be blank");
            }
            smtp.setUsername(smtpRequest.getUsername());
            smtp.setPassword(smtpRequest.getPassword());
            emailKbServiceRepository.save(smtp);
            return "successfully update smtp";
        }else{
            throw new NotFoundException("Config Id is not found");
        }
    }
}
