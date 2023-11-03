package com.example.clienteventservice.service;

import com.example.clienteventservice.exception.AlreadyExistException;
import com.example.clienteventservice.exception.BadRequestException;
import com.example.clienteventservice.exception.ForbiddenException;
import com.example.clienteventservice.exception.NotFoundException;
import com.example.clienteventservice.model.entity.Subscription;
import com.example.clienteventservice.repository.SubscriptionRepository;
import com.example.dto.UserDtoClient;
import com.example.clienteventservice.model.entity.User;
import com.example.clienteventservice.model.request.LoginRequest;
import com.example.clienteventservice.model.request.ProfileRequest;
import com.example.clienteventservice.model.request.UserRequest;
import com.example.response.ApiResponse;
import com.example.response.LoginResponse;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import javax.naming.AuthenticationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
//@AllArgsConstructor
public class UserService {


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

    @Value("${image.url}")
    private String url;

    @Value("${LoginPage.url}")
    private String loginPageUrl;

    @Value("${forgetPasswordPage.url}")
    private String forgetPasswordPageUrl;

    @Value("${ExpiredPage.url}")
    private String expiredPageUrl;

    @Value("${telegram.url}")
    private String telegramUrl;


    private static final String NOTIFICATION_TOPIC = "notification";
    private final WebClient.Builder webClient;
    private final EmailService emailService;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Keycloak keycloak;;
    private final SubscriptionRepository subscriptionRepository;

    public UserService(WebClient.Builder webClient, EmailService emailService, RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate, Keycloak keycloak, SubscriptionRepository subscriptionRepository) {
        this.webClient = webClient;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.keycloak = keycloak;
        this.subscriptionRepository = subscriptionRepository;
    }


    public ApiResponse<?> sendMessage(String message, Jwt jwt) {
        return webClient.baseUrl(telegramUrl + "/send-message")
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(jwt.getTokenValue()))
                .build()
                .post()
                .uri("?message="+message)
                .retrieve()
                .bodyToMono(ApiResponse.class).block();
    }

    public ApiResponse<LoginResponse> login(LoginRequest loginrequest) {
        UserRepresentation userRepresentation = getUserRepresentationByEmail(loginrequest.getEmail());
        if (!Boolean.parseBoolean(userRepresentation.getAttributes().get("isVerify").get(0))) {
            throw new BadRequestException("user not yet verify code yet");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", secretKey);
            map.add("grant_type", grantType);
            map.add("username", loginrequest.getEmail());
            map.add("password", loginrequest.getPassword());

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

            return ApiResponse.<LoginResponse>builder()
                    .message("login success...!!")
                    .loginResponse(restTemplate.postForEntity(tokenUrl, httpEntity, LoginResponse.class).getBody())
                    .status(200).build();

        } catch (Exception e) {
            throw new BadRequestException("incorrect password");
        }
    }


    public List<UserDtoClient> getAllUsers() {
        return keycloak.realm(realm).users().list().stream()
                .map(e -> User.toDto(e, url))
                .toList();
    }

    public ApiResponse<?> create(UserRequest userRequest) {

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

        UserRepresentation userRepresentation = prepareUserRepresentation(userRequest, preparePasswordRepresentation(userRequest.getPassword()));
        UsersResource userResource = keycloak.realm(realm).users();
        Response response = userResource.create(userRepresentation);

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new AlreadyExistException("email is already exist");
        }

        emailService.sendSimpleMail(userRequest.getUsername(), userRequest.getEmail(), 1);
        return ApiResponse.<UserDtoClient>builder()
                .message("register success..!")
                .payload(getByEmail(userRequest.getEmail()))
                .status(200)
                .build();
    }



    private CredentialRepresentation preparePasswordRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);
        return credentialRepresentation;
    }

    public UserRepresentation prepareUserRepresentation(UserRequest userRequest, CredentialRepresentation credentialRepresentation) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setEmail(userRequest.getEmail());

        userRepresentation.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));

        userRepresentation.singleAttribute("profile", "DefaultProfile.jpeg");
        userRepresentation.singleAttribute("isVerify", "false");

        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    public UserRepresentation prepareUserRepresentationForProfile(UserRepresentation user, ProfileRequest profileRequest) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(profileRequest.getUsername());

        userRepresentation.singleAttribute("createdDate", user.getAttributes().get("createdDate").get(0));
        userRepresentation.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("profile", profileRequest.getProfile());
        userRepresentation.singleAttribute("isVerify", "true");

        userRepresentation.setCredentials(Collections.singletonList(preparePasswordRepresentation(profileRequest.getNewPassword())));
        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    public UserRepresentation prepareUserRepresentationForUpdate(UserRepresentation userRequest, String isVerify, String isForget, int index) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setEmail(userRequest.getEmail());

        userRepresentation.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("profile", "DefaultProfile.jpeg");

        userRepresentation.singleAttribute("isVerify", isVerify);

        if (index == 2) {
            userRepresentation.singleAttribute("isForget", isForget);
            userRepresentation.singleAttribute("forget_createAt", String.valueOf(LocalDateTime.now()));
        }

        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    public UserRepresentation prepareUserRepresentationForgetPassword(UserRepresentation userRequest, CredentialRepresentation credentialRepresentation, String createDate) {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setEmail(userRequest.getEmail());

        userRepresentation.singleAttribute("profile", "DefaultProfile.jpeg");
        userRepresentation.singleAttribute("createdDate", String.valueOf(createDate));
        userRepresentation.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userRepresentation.singleAttribute("isVerify", "true");
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        userRepresentation.setEnabled(true);
        return userRepresentation;
    }

    private UserRepresentation prepareUserRepresentationForVerifyCode(UserRepresentation request, String isVerify, String isForget, String type) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());

        newUser.singleAttribute("profile", "DefaultProfile.jpeg");
        newUser.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        newUser.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        newUser.singleAttribute("isVerify", isVerify);
        if (type.equals("2")) {
            newUser.singleAttribute("isForget", isForget);
            newUser.singleAttribute("forget_createAt", String.valueOf(LocalDateTime.now()));
        }
        newUser.setEnabled(true);
        return newUser;
    }

    public UserDtoClient getByEmail(String email) {
        return User.toDto(getUserRepresentationByEmail(email.trim()), url);
    }

    public UserRepresentation getUserRepresentationByEmail(String email) {
        List<UserRepresentation> users = keycloak.realm(realm).users().searchByEmail(email, true);
        if (users.isEmpty()) {
            throw new NotFoundException("email : " + email + " is not found..!!");
        }
        return users.get(0);
    }

    public RedirectView verifyEmail(String email, String type) {
        String typeCreate = "createdDate";
        String url = loginPageUrl;
        if (type.equals("2")) {
            typeCreate = "forget_createAt";
            url = forgetPasswordPageUrl;
        }
        try {
            UserRepresentation user = checkLinkExpired(email, typeCreate);
            UserRepresentation userRepresentation = prepareUserRepresentationForVerifyCode(user, "true", "true", type);
            UsersResource userResource = keycloak.realm(realm).users();
            userResource.get(user.getId()).update(userRepresentation);
            return new RedirectView(url);
        } catch (Exception e) {
            return new RedirectView(expiredPageUrl);
        }
    }

    public UserDtoClient getByUserName(String username) {
        List<UserRepresentation> user = keycloak.realm(realm).users().searchByUsername(username.trim(), true);
        if (user.isEmpty()) {
            throw new NotFoundException("username : " + username + " is not found..!!");
        }
        return User.toDto(user.get(0), url);
    }

    public ApiResponse<?> forgetPassword(String email, String newPassword) {
        UserRepresentation user = getUserRepresentationByEmail(email.trim());
        if (user.getAttributes().get("isForget") == null) {
            throw new BadRequestException("you not yet verify email for reset new password");
        } else {
            if (user.getAttributes().get("isForget").get(0).equals("false")) {
                throw new BadRequestException("you not yet verify email for reset new password2");
            }
        }

        //validate password
        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException(
                    "Password should be at least 8 character and 1 special character Uppercase and Lowercase character and No Space"
            );
        }
        UserRepresentation userRepresentation = prepareUserRepresentationForgetPassword(user, preparePasswordRepresentation(newPassword), user.getAttributes().get("createdDate").get(0));
        UsersResource userResource = keycloak.realm(realm).users();
        userResource.get(user.getId()).update(userRepresentation);
        return ApiResponse.builder()
                .message("reset password Success")
                .status(200)
                .build();
    }

    public UserRepresentation checkLinkExpired(String email, String typeCreate) {
        UserRepresentation user = getUserRepresentationByEmail(email);

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = formatter.format(now);
        LocalDateTime optCreated = LocalDateTime.parse(user.getAttributes().get(typeCreate).get(0));
        LocalTime time1 = LocalTime.parse(formattedTime);
        LocalTime time2 = optCreated.toLocalTime();

        Duration duration = Duration.between(time2, time1);
        long minutes = duration.toMinutes() % 60;

        //expired 1 minute
        if (minutes >= 1) {
            throw new BadRequestException("link is expired");
        }
        return user;
    }

    public ApiResponse<?> generateEmailForgetPassword(String email) {
        return generateLinkVerifyEmail(email.trim(), "true", 2, "false");
    }

    public ApiResponse<?> generateLinkVerifyEmail(String email, String isVerify, Integer index, String isForget) {
        UserRepresentation user = getUserRepresentationByEmail(email.trim());
        UserRepresentation userRepresentation = prepareUserRepresentationForUpdate(user, isVerify, isForget, index);
        UsersResource userResource = keycloak.realm(realm).users();
        userResource.get(user.getId()).update(userRepresentation);
        emailService.sendSimpleMail(user.getUsername(), user.getEmail(), index);
        return ApiResponse.builder()
                .message("verify email success")
                .status(200)
                .build();
    }

    public UserRepresentation getUserRepresentationById(UUID id) {
        try {
            return keycloak.realm(realm).users().get(String.valueOf(id)).toRepresentation();
        } catch (Exception e) {
            throw new NotFoundException("user id : " + id + " is not found");
        }
    }
    public UserDtoClient getById(UUID id) {
        return User.toDto(getUserRepresentationById(id), url);
    }

    public ApiResponse<?> getInfo(Principal principal) {
        if (principal==null){
            throw new ForbiddenException("need token");
        }
        return ApiResponse.builder()
                .message("get user by id success")
                .payload(User.toDto(getUserRepresentationById(UUID.fromString(principal.getName())), url))
                .status(200)
                .build();
    }


    public ApiResponse<?> updateById( ProfileRequest userRequest, Principal principal,Jwt jwt) {
        if (principal==null){
            throw new ForbiddenException("need token");
        }
        try {
             User.toDto(getUserRepresentationById(UUID.fromString(principal.getName())), url);
        }catch (Exception e){
            throw new ForbiddenException("user need to login");
        }

        UserRepresentation user = getUserRepresentationById(UUID.fromString(principal.getName()));
        if (userRequest.getProfile().isEmpty() || userRequest.getProfile().isBlank()) {
            throw new BadRequestException(
                    "profile can not empty"
            );
        }

        if (!userRequest.getNewPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new BadRequestException(
                    "Password should be at least 8 character and 1 special character Uppercase and Lowercase character and No Space"
            );
        }

        try {

            UserRepresentation userRepresentation = prepareUserRepresentationForProfile(user, userRequest);
            UsersResource userResource = keycloak.realm(realm).users();
            userResource.get(user.getId()).update(userRepresentation);
            sendMessage("you have been update your information already",jwt);
            return ApiResponse.builder()
                    .message("update user by id success")
                    .payload(User.toDto(getUserRepresentationById(UUID.fromString(principal.getName())), url))
                    .status(200)
                    .build();
        }
        catch (Exception e) {
            throw new BadRequestException("username already exist");
        }
    }

//    public ApiResponse<?> chooseNotificationTypes(NotificationType notificationType, Principal principal) {
//        // Assuming you have a method to get the user representation by ID and a URL
//        UserDtoClient userDTOClient = User.toDto(getUserRepresentationById(UUID.fromString(principal.getName())), url);
//
//        saveUserPreferences(userDTOClient.getId().toString(), notificationType);
//
//        // Return a successful response with user data
//        return ApiResponse.builder()
//                .message("user choose notification type successfully!")
//                .payload(User.toDto(getUserRepresentationById(UUID.fromString(principal.getName())), url))
//                .status(200)
//                .build();
//    }


//    public void saveUserPreferences(String userId, NotificationType notificationType) {
//        try {
//            RealmResource realmResource = keycloak.realm(realm);
//            UserResource userResource = realmResource.users().get(userId);
//            UserRepresentation userRepresentation = userResource.toRepresentation();
//
//            // Create or update the user's custom attributes
//            userRepresentation.getAttributes().put("notificationType", Collections.singletonList(notificationType.name()));
//
//            // Update the user using the Keycloak Admin API
//            userResource.update(userRepresentation);
//
//        } catch (Exception e) {
//            // Handle exceptions
//            e.printStackTrace();
//        }
//    }


    public ApiResponse<?> sendMoney(String sendMoney, Principal principal) {

        if (principal == null) {
            // User is not authenticated; return an error response
            return new ApiResponse<>(HttpStatus.FORBIDDEN, "Authentication required");
        }

        try {
            // Validate the input amount (e.g., ensure it's a valid numeric value)
            BigDecimal amount = new BigDecimal(sendMoney);

            // Get the sender's user information (e.g., balance, user ID)
            User sender = getUserByPrincipal(principal);

            // Check if the sender has sufficient funds to complete the transaction
//            BigDecimal senderBalance = sender.getBalance();

            // Deduct the amount from the sender's balance
//            sender.setBalance(senderBalance.subtract(amount));


            String _sender = "user id: ";
            String _userId = principal.getName();
            String _action = " has send money.";

            List<Subscription> subscriptions = subscriptionRepository.findAll();

            for (Subscription subscription : subscriptions) {
                String subscriptionUserId = subscription.getUserId();

                if (subscriptionUserId.equals(_userId)) {
                    // Send a notification to the Kafka topic
                    String notificationMessage = _sender + subscriptionUserId + _action;
                    sendNotificationToKafka(_userId, notificationMessage);
                }
            }

            // Return a successful response with user data
            return ApiResponse.builder()
                    .message("send money successfully!")
                    .payload(User.toDto(getUserRepresentationById(UUID.fromString(principal.getName())), url))
                    .status(200)
                    .build();
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    private User getUserByPrincipal(Principal principal) throws AuthenticationException {
        if (principal == null) {
            throw new AuthenticationException("Principal is null");
        }

        String principalName = principal.getName();

        UserDtoClient userDTOClient = User.toDto(getUserRepresentationById(UUID.fromString(principal.getName())), url);

        User user = new User();
        user.setId(userDTOClient.getId());
        user.setUsername(principalName);

//        String balanceAsString = userDTOClient.getBalance();
//        BigDecimal balance = new BigDecimal(balanceAsString);
//        user.setBalance(balance); // Set the user's balance

        return user;
    }

    private void sendNotificationToKafka(String userId, String message) {
        kafkaTemplate.send(NOTIFICATION_TOPIC, userId, message);
    }

}
