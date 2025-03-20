package com.example.UserManagementService.controller;

import com.example.UserManagementService.config.exceptions.DataNotFoundException;
import com.example.UserManagementService.model.Organizer;
import com.example.UserManagementService.model.dto.update.UpdateOrganizerDTO;
import com.example.UserManagementService.service.OrganizerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizers")
@Slf4j
public class OrganizerController {

    @Autowired
    private OrganizerService organizerService;

    // obtener los datos del organizador autenticado
    @GetMapping("/me")
    public ResponseEntity<?> getPlayerData(@RequestHeader("Authorization") String authorizationHeader) {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            Organizer organizer = organizerService.getOrganizerData(userId);
            return ResponseEntity.ok(organizer);
        } catch (DataNotFoundException e){
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar datos del organizador autenticado
    @PutMapping("/me")
    public ResponseEntity<?> updatePlayerData(@RequestBody UpdateOrganizerDTO updateOrganizerDTO) {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            organizerService.updateOrganizerData(userId, updateOrganizerDTO);
            return ResponseEntity.ok().build();
        } catch (DataNotFoundException e){
            log.error(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
