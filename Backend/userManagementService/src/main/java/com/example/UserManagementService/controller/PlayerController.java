package com.example.UserManagementService.controller;

import com.example.UserManagementService.config.JwtUtil;
import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/get_player")
    public ResponseEntity<?> getPlayerData(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado o mal formado");
        }

        String jwt = authorizationHeader.substring(7);
        long userId;
        try {
            userId = Long.parseLong(jwtUtil.getIdFromToken(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        Player player = playerService.getPlayerData(userId);
        if (player == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Jugador no encontrado");
        }

        return ResponseEntity.ok(player);
    }
}
