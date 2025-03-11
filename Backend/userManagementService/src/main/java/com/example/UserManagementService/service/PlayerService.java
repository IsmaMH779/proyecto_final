package com.example.UserManagementService.service;

import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    @Autowired
    PlayerRepository playerRepository;

    @Transactional
    public void savePlayer(PlayerRegisterDTO playerDTO, Long userId) {
        Player player = new Player();

        player.setId(userId);
        player.setRole("player");
        player.setUsername(playerDTO.getUsername());
        player.setMail(playerDTO.getMail());
        player.setLocation(playerDTO.getLocation());

        playerRepository.save(player);
    }

    public Player getPlayerData(long userId) {
        Player player = playerRepository
                .findById(userId).orElseThrow(() -> new EntityNotFoundException("Player not found with id: " + userId));

        return player;
    }
}
