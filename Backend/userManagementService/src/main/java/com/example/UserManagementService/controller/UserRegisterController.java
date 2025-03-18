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
    @Autowired
    private OrganizerService organizerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private JwtUtil jwtUtil;

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
        OrganizerService.saveOrganizer(playerRegisterDTO, userId);

        return ResponseEntity.ok("Organizador registrado");
    }
}
