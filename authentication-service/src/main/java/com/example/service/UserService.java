package com.example.service;

import com.example.model.entity.User;
import com.example.model.request.LoginRequest;
import com.example.model.request.UserRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.microservice.fintrack.dto.UserDto;
import org.microservice.fintrack.exception.AlreadyExistException;
import org.microservice.fintrack.exception.BadRequestException;
import org.microservice.fintrack.exception.NotFoundException;
import org.microservice.fintrack.response.ApiResponse;
import org.microservice.fintrack.response.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class UserService {
    private final Keycloak keycloak;
    private final EmailService emailService;
    private final RestTemplate restTemplate;
    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.credentials.secret}")
    private String secretKey;
    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.tokenUrl}")
    private String tokenUrl;

    @Value("${keycloak.grant-type}")
    private String grantType;

    public UserService(Keycloak keycloak, EmailService emailService, RestTemplate restTemplate) {
        this.keycloak = keycloak;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }

    public ApiResponse<LoginResponse> login(LoginRequest loginrequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", secretKey);
        map.add("grant_type", grantType);
        map.add("username", loginrequest.getEmail());
        map.add("password", loginrequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);
        UserRepresentation userRepresentation = getUserRepresentationByEmail(loginrequest.getEmail());

        if (!Boolean.parseBoolean(userRepresentation.getAttributes().get("isVerify").get(0))) {
            throw new BadRequestException("user not yet verify code yet");
        }

        return ApiResponse.<LoginResponse>builder()
                .message("login success...!!")
                .loginResponse(restTemplate.postForEntity(tokenUrl, httpEntity, LoginResponse.class).getBody())
                .status(HttpStatus.OK).build();
    }

    public String randomCode() {
        Random rand = new Random();
        return String.valueOf(rand.nextInt(900000) + 100000);
    }

    public List<UserDto> getAllUsers() {
        return keycloak.realm(realm).users().list().stream()
                .map(User::toDto)
                .toList();
    }

    public ApiResponse<UserDto> create(UserRequest userRequest) {

        if (userRequest.getUsername().isEmpty() || userRequest.getUsername().isBlank()) {
            throw new BadRequestException(
                    "username can not empty"
            );
        }
        if (!keycloak.realm(realm).users().searchByUsername(userRequest.getUsername(), true).isEmpty()) {
            throw new AlreadyExistException("username is already exist");
        }
//       validate email
        if (!userRequest.getEmail().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
            throw new BadRequestException(
                    "Email should be like this -> somthing@somthing.com"
            );
        }
        //validate password
        if (!userRequest.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException(
                    "Password should be at least 8 character and 1 special character Uppercase and Lowercase character and No Space"
            );
        }

        UserRepresentation userRepresentation = prepareUserRepresentation(userRequest, preparePasswordRepresentation(userRequest.getPassword()), randomCode());
        UsersResource userResource = keycloak.realm(realm).users();
        Response response = userResource.create(userRepresentation);

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new AlreadyExistException("email is already exist");
        }

        emailService.sendSimpleMail(userRequest.getUsername(), userRequest.getEmail(), randomCode(),1);
        return ApiResponse.<UserDto>builder()
                .message("register success..!")
                .payload(getByEmail(userRequest.getEmail()))
                .status(HttpStatus.OK)
                .build();
    }

    private CredentialRepresentation preparePasswordRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }

    public UserRepresentation prepareUserRepresentation(UserRequest userRequest, CredentialRepresentation credentialRepresentation, String optCode) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setEmail(userRequest.getEmail());

        userRepresentation.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("optCode", optCode);
        userRepresentation.singleAttribute("isVerify", "false");
        userRepresentation.singleAttribute("optCreated", String.valueOf(LocalDateTime.now()));

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    public UserRepresentation prepareUserRepresentationForUpdate(UserRepresentation userRequest, String optCode) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setEmail(userRequest.getEmail());

        userRepresentation.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("optCode", optCode);
        userRepresentation.singleAttribute("isVerify", "false");
        userRepresentation.singleAttribute("optCreated", String.valueOf(LocalDateTime.now()));

        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    public UserRepresentation prepareUserRepresentationForgetPassword(UserRepresentation userRequest, CredentialRepresentation credentialRepresentation, String createDate) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setEmail(userRequest.getEmail());

        userRepresentation.singleAttribute("createdDate", String.valueOf(createDate));
        userRepresentation.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("isVerify", "true");
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    private UserRepresentation prepareUserRepresentationForVerifyCode(UserRepresentation request, String isVerify) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        newUser.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));

        newUser.singleAttribute("isVerify", isVerify);
        newUser.setEnabled(true);
        return newUser;
    }

    public UserDto getByEmail(String email) {
        return User.toDto(getUserRepresentationByEmail(email));
    }

    public UserRepresentation getUserRepresentationByEmail(String email) {
        List<UserRepresentation> users = keycloak.realm(realm).users().searchByEmail(email, true);
        if (users.isEmpty()) {
            throw new NotFoundException("email : " + email + " is not found..!!");
        }
        return users.get(0);
    }


    public ApiResponse<?> verifyCode(String email, String code) {
        UserRepresentation user = checkExpiredCode(email, code);
        UserRepresentation userRepresentation = prepareUserRepresentationForVerifyCode(user, "true");
        UsersResource userResource = keycloak.realm(realm).users();
        userResource.get(user.getId()).update(userRepresentation);
        return ApiResponse.builder()
                .message("verify opt code success,use can login your account")
                .status(HttpStatus.OK)
                .build();
    }


    public ApiResponse<?> getByUserName(String username) {
        List<UserRepresentation> user = keycloak.realm(realm).users().searchByUsername(username, true);
        if (user.isEmpty()) {
            throw new NotFoundException("username : " + username + " is not found..!!");
        }
        return ApiResponse.builder()
                .message("get user by username success")
                .payload(User.toDto(user.get(0)))
                .status(HttpStatus.OK)
                .build();
    }

    public ApiResponse<?> forgetPassword(String email, String code, String newPassword) {
        UserRepresentation user = checkExpiredCode(email, code);
        UserRepresentation userRepresentation = prepareUserRepresentationForgetPassword(user,preparePasswordRepresentation(newPassword), user.getAttributes().get("createdDate").get(0));
        UsersResource userResource = keycloak.realm(realm).users();
        userResource.get(user.getId()).update(userRepresentation);
        return ApiResponse.builder()
                .message("reset password Success")
                .status(HttpStatus.OK)
                .build();
    }

    public UserRepresentation checkExpiredCode(String email, String code) {
        UserRepresentation user = getUserRepresentationByEmail(email);
        if (!user.getAttributes().get("optCode").get(0).equals(code)) {
            throw new NotFoundException("code: " + code + " is not found...!!");
        }
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = formatter.format(now);
        LocalDateTime optCreated = LocalDateTime.parse(user.getAttributes().get("optCreated").get(0));

        LocalTime time1 = LocalTime.parse(formattedTime);
        LocalTime time2 = optCreated.toLocalTime();

        Duration duration = Duration.between(time2, time1);
        long minutes = duration.toMinutes() % 60;

        //expired 1 minute
        if (minutes >= 1) {
            throw new BadRequestException("code is expired");
        }
        return user;
    }
    public ApiResponse<?> generateCodeForgetPassword(String email){
        return generateCode(email,2);
    }

    public ApiResponse<?> generateCode(String email,Integer index) {
        UserRepresentation user = getUserRepresentationByEmail(email);
        UserRepresentation userRepresentation = prepareUserRepresentationForUpdate(user, randomCode());
        UsersResource userResource = keycloak.realm(realm).users();
        userResource.get(user.getId()).update(userRepresentation);
        emailService.sendSimpleMail(user.getUsername(), user.getEmail(), randomCode(),index);
        return ApiResponse.builder()
                .message("generate code success")
                .status(HttpStatus.OK)
                .build();
    }
}
