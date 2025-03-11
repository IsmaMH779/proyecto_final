package com.example.UserManagementService.controller;

import com.example.UserManagementService.config.JwtUtil;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.service.OrganizerService;
import com.example.UserManagementService.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user_management")
public class UserRegisterController {
    @Autowired
    private OrganizerService organizerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/player_register")
    private ResponseEntity<String> playerRegister(@RequestBody PlayerRegisterDTO playerRegisterDTO, @RequestHeader("Authorization") String authorizationHeader) {

        // Verifica que el header tiene el formato correcto
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token no proporcionado o inválido");
        }

        // Extraer el token
        String jwt = authorizationHeader.substring(7);

        // Extraer el userId desde el token
        Long userId;
        try {
            userId = Long.parseLong(jwtUtil.getIdFromToken(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        // Llamar al servicio
        playerService.savePlayer(playerRegisterDTO, userId);

        return ResponseEntity.ok("jugador registrado");
    }

}
