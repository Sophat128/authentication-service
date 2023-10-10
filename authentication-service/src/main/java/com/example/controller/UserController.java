package com.example.controller;

import com.example.model.request.ProfileRequest;
import com.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("username")
    public ResponseEntity<?> getByUsername(@RequestParam String username){
        return ResponseEntity.ok().body(userService.getByUserName(username));
    }
    @GetMapping("email")
    public ResponseEntity<?> getByEmail(@RequestParam String email){
        return ResponseEntity.ok().body(userService.getByEmail(email));
    }

    @GetMapping
    public ResponseEntity<?> getAllUser(){
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable UUID id, @RequestBody ProfileRequest userRequest){
        return ResponseEntity.ok().body(userService.updateById(id,userRequest));
    }
}
