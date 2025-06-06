package com.example.authService.controller;

import com.example.authService.config.JwtUtil;
import com.example.authService.config.exceptions.DeckLyException;
import com.example.authService.config.exceptions.NotValidDataException;
import com.example.authService.model.dto.UserDTOLogin;
import com.example.authService.model.dto.UserDTORegister;
import com.example.authService.model.dto.UserUpdateDTO;
import com.example.authService.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTORegister userDTORegister) {
        try {
            String serviceResponse = authService.registerPlayer(userDTORegister);
            String token = jwtUtil.generateToken(serviceResponse);
            return ResponseEntity.ok(token);
        }catch (NotValidDataException e){
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/organizer")
    public ResponseEntity<String> registerOrganizer(@RequestBody UserDTORegister userDTORegister) {
        try {
            String serviceResponse = authService.registerOrganizer(userDTORegister);
            String token = jwtUtil.generateToken(serviceResponse);
            return ResponseEntity.ok(token);
        }catch (NotValidDataException e){
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTOLogin userDTOLogin) {
        try {
            String token = authService.loginUser(userDTOLogin.getEmail(), userDTOLogin.getPassword());
            return ResponseEntity.ok(token);
        } catch (NotValidDataException e) {
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserData(@RequestBody UserUpdateDTO userUpdateDTO) {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            authService.updateUser(userId, userUpdateDTO);
            return ResponseEntity.ok().build();
        } catch (DeckLyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me/role")
    public ResponseEntity<?> getRole() {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            return ResponseEntity.ok().body(authService.getUser(userId));
        } catch (DeckLyException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}