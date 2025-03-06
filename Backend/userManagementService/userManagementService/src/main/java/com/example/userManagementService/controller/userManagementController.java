package com.example.userManagementService.controller;

import com.example.userManagementService.config.JwtUtil;
import com.example.userManagementService.model.dto.PlayerDTO;
import com.example.userManagementService.security.CustomUserDetails;
import com.example.userManagementService.service.UserManagementService;
import com.example.userManagementService.model.dto.RegisterPlayerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            userManagementService.registerPlayer(registerPlayerDTO, String.valueOf(userId));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/get-user-data")
    public ResponseEntity<PlayerDTO> getUserData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Aquí puedes obtener los datos del usuario autenticado usando su ID
        String userId = userDetails.getUserId();
        PlayerDTO userData = userManagementService.getUserData(userId);
        return ResponseEntity.ok(userData);
    }

}