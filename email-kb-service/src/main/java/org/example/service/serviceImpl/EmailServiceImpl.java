package org.example.service.serviceImpl;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.example.exception.BadRequestException;
import org.example.exception.NotFoundException;
import org.example.model.Email;
import org.example.model.Smtp;
import org.example.model.SmtpDto;
import org.example.model.request.SmtpRequest;
import org.example.repository.EmailKbServiceRepository;
import org.example.service.EmailKbService;
import org.example.service.MailSenderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.context.Context;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

    @Value("${smtp.username}")
    private String smtpUsername;
    @Value("${smtp.password}")
    private String smtpPassword;

    @PostConstruct
    public void init(){
        if(emailKbServiceRepository.findAll().isEmpty()){
            Smtp smtp = new Smtp();
            smtp.setUsername(smtpUsername);
            smtp.setPassword(smtpPassword);
            emailKbServiceRepository.save(smtp);
        }else{
            System.out.println("already add it ");
        }
    }


//    public UserDto getUserById(UUID userId) {
//        return webClient.get()
//                .uri("/users/{userId}", userId)
//                .retrieve()
//                .bodyToMono(UserDto.class)
//                .block();
//    }


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



//    @Override
//    public SmtpDto configEmail(SmtpRequest smtpRequest) {
//
//        if (smtpRequest.getUsername().isBlank() || smtpRequest.getUsername().isEmpty()) {
//            throw new BadRequestException("Field username can't be blank");
//        }
//
//        if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
//            throw new BadRequestException("Username should be like this -> something@something.com");
//        }
//
//        if (smtpRequest.getPassword().isBlank() || smtpRequest.getPassword().isEmpty()) {
//            throw new BadRequestException("Field password can't be blank");
//        }
//        Smtp smtp = new Smtp();
//        smtp.setUsername(smtpRequest.getUsername());
//        smtp.setPassword(smtpRequest.getPassword());
//        emailKbServiceRepository.save(smtp) ;
//        SmtpDto smtpDto = new SmtpDto();
//        smtpDto.setId(smtp.getId());
//        smtpDto.setUsername(smtp.getUsername());
//        return smtpDto;
//    }

    @Override
    public String updateConfigEmail(SmtpRequest smtpRequest) {
        List<Smtp> smtp = emailKbServiceRepository.findAll();
        try {
            Smtp smtpUpdate = smtp.get(0);
            if (smtpRequest.getUsername().isBlank() || smtpRequest.getUsername().isEmpty()) {
                throw new BadRequestException("Field username can't be blank");
            }

            if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
                throw new BadRequestException("Username should be like this -> something@something.com");
            }

            if (smtpRequest.getPassword().isBlank() || smtpRequest.getPassword().isEmpty()) {
                throw new BadRequestException("Field password can't be blank");
            }
            smtpUpdate.setUsername(smtpRequest.getUsername());
            smtpUpdate.setPassword(smtpRequest.getPassword());
            emailKbServiceRepository.save(smtpUpdate);
            return "successfully update smtp";
        }catch (Exception e){
            throw new NotFoundException("Config is not create yet");
        }
    }

    @Override
    public List<SmtpDto> getConfigEmail() {
        List<Smtp> smtpList = emailKbServiceRepository.findAll();
        List<SmtpDto> smtpDtoList = new ArrayList<>();

        if (!smtpList.isEmpty()) {
            Smtp smtp = smtpList.get(0);

            SmtpDto smtpDto = new SmtpDto();
            smtpDto.setId(smtp.getId());
            smtpDto.setUsername(smtp.getUsername());
            smtpDtoList.add(smtpDto);
        }

        return smtpDtoList;
    }


}
