package com.example.UserManagementService.controller;

import com.example.UserManagementService.config.exceptions.DataNotFoundException;
import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.model.dto.update.UpdatePlayerDTO;
import com.example.UserManagementService.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/players")
@Slf4j
public class PlayerController {

    @Autowired
    private PlayerService playerService;


    // obtener los datos del jugador autenticado
    @GetMapping("/me")
    public ResponseEntity<?> getPlayerData() {
        //preguntar al yago si eliminar esto despues de pasarlo al player
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            Player player = playerService.getPlayerData(userId);
            return ResponseEntity.ok(player);
        } catch (DataNotFoundException e){
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar datos del jugador autenticado
    @PutMapping("/me")
    public ResponseEntity<?> updatePlayerData(@RequestBody UpdatePlayerDTO updatePlayerDTO) {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            playerService.updatePlayerData(userId, updatePlayerDTO);
            return ResponseEntity.ok().build();
        } catch (DataNotFoundException e){
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // para guardar la foto de perfil del usuario
    @PostMapping("/me/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam MultipartFile file) {
        try {
            String fileName = playerService.updateProfileImage(file);
            return ResponseEntity.ok(Map.of("imageUrl", fileName));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(e);
        }
    }
}
