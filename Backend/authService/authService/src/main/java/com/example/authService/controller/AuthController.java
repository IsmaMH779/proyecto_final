package com.example.authService.controller;

import com.example.authService.service.AuthService;
import com.example.authService.model.User;
import com.example.authService.model.dto.UserDTOLogin;
import com.example.authService.model.dto.UserDTORegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTORegister userDTORegister) {
        User user = authService.registerPlayer(userDTORegister);
        return ResponseEntity.ok("Usuario registrado");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTOLogin userDTOLogin) {
        String token = authService.loginUser(userDTOLogin.getEmail(), userDTOLogin.getPassword());
        return ResponseEntity.ok(token);
    }
}