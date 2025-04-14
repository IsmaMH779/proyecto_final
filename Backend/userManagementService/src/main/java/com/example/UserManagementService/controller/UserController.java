package com.example.UserManagementService.controller;

import com.example.UserManagementService.config.JwtUtil;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.service.OrganizerService;
import com.example.UserManagementService.service.PlayerService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/user-management")
public class UserController {
    private final OrganizerService organizerService;
    private final PlayerService playerService;
    private final JwtUtil jwtUtil;

    public UserController(OrganizerService organizerService, PlayerService playerService, JwtUtil jwtUtil) {
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

    // obtener la imagen de perfil
    @GetMapping("/images/profile/{filename:.+}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) throws IOException {
        // preparar la ruta de la imagen
        Path filePath = Paths.get("backend/uploads/profile_pics/", filename);
        // comprobar que existe la imagen
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        // preparar la imagen como resource
        Resource resource = new UrlResource(filePath.toUri());
        // detecar el tipo de archivo
        String contentType = Files.probeContentType(filePath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .body(resource);
    }
}
