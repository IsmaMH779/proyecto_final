package com.example.authService.controller;

import com.example.authService.config.JwtUtil;
import com.example.authService.model.dto.PlayerDTO;
import com.example.authService.service.UserManagementService;
import com.example.authService.model.dto.RegisterPlayerDTO;
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
    public ResponseEntity<String> playerRegister(@RequestBody RegisterPlayerDTO registerPlayerDTO) {
        // si el token recibido es true guardara el usuario
        if (jwtUtil.validateToken(registerPlayerDTO.getToken())) {
            try {
                userManagementService.registerPlayer(registerPlayerDTO);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/get-user-data")
    public ResponseEntity<PlayerDTO> getResource(@RequestHeader("Authorization") String token) {
        // verificar que el token es valido
        if (jwtUtil.validateToken((token))){
            // si es valido manda el codigo 200 junto al userData
            PlayerDTO userData = userManagementService.getUserData(token);
            return ResponseEntity.ok(userData);
        } else {
            // si no es valido, devuelve el codigo 401 (unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
    }

}