package com.example.authService.controller;

import com.example.authService.config.JwtUtil;
import com.example.authService.model.dto.UserDTO;
import com.example.authService.service.UserManagementService;
import com.example.authService.model.dto.UserDTORegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-Management")
public class userManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/player-register")
    public ResponseEntity<String> playerRegister(@RequestBody UserDTORegister userDTORegister) {
        // si el token recibido es true guardara el usuario
        if (jwtUtil.validateToken(userDTORegister.getToken())) {
            try {
                userManagementService.registerPlayer(userDTORegister);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/get-user-data")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {




        return ResponseEntity.ok(token);
    }
}