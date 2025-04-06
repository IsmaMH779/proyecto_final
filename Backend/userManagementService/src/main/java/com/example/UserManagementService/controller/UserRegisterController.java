package com.example.UserManagementService.controller;

import com.example.UserManagementService.config.JwtUtil;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.service.OrganizerService;
import com.example.UserManagementService.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-management")
public class UserRegisterController {
    private final OrganizerService organizerService;
    private final PlayerService playerService;
    private final JwtUtil jwtUtil;

    public UserRegisterController(OrganizerService organizerService, PlayerService playerService, JwtUtil jwtUtil) {
        this.organizerService = organizerService;
        this.playerService = playerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/players/register")
    private ResponseEntity<String> playerRegister(@RequestBody PlayerRegisterDTO playerRegisterDTO) {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        playerService.savePlayer(playerRegisterDTO, userId);

        return ResponseEntity.ok("jugador registrado");
    }

    @PostMapping("/organizers/register")
    private ResponseEntity<String> organizerRegister(@RequestBody PlayerRegisterDTO playerRegisterDTO, @RequestHeader("Authorization") String authorizationHeader) {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Llamar al servicio
        organizerService.saveOrganizer(playerRegisterDTO, userId);

        return ResponseEntity.ok("Organizador registrado");
    }
}
