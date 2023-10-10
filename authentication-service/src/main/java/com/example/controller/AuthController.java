package com.example.controller;


import com.example.model.request.LoginRequest;
import com.example.model.request.UserRequest;
import com.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        return  ResponseEntity.ok().body( userService.create(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok().body(userService.login(loginRequest));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String optCode){
        return ResponseEntity.ok().body(userService.verifyCode(email, optCode));
    }
    @PostMapping("/generate-code")
    public ResponseEntity<?> generateCode(@RequestParam String email){
        return ResponseEntity.ok().body(userService.generateCode(email,1));
    }
    @PostMapping("/generate-code-forget-password")
    public ResponseEntity<?> generateCodeForget(@RequestParam String email){
        return ResponseEntity.ok().body(userService.generateCodeForgetPassword(email));
    }
    @PutMapping("/forget-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email,@RequestParam String code,@RequestParam String newPassword){
        return ResponseEntity.ok().body(userService.forgetPassword(email,code,newPassword));
    }

}
