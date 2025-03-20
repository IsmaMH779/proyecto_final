package com.example.UserManagementService.service;

import com.example.UserManagementService.config.exceptions.DataNotFoundException;
import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.model.dto.update.UpdatePlayerDTO;
import com.example.UserManagementService.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        Optional<Player> player = playerRepository.findById(userId);

        if (player.isPresent()) {
            return player.get();
        } else {
            throw DataNotFoundException.of("PLAYER_NOT_EXISTS");
        }
    }


    public void updatePlayerData(long userId, UpdatePlayerDTO updatePlayerDTO) {
        //primero obtener el jugador
        Player player = getPlayerData(userId);

        // acutalizar username
        if (updatePlayerDTO.getUsername() != null && !updatePlayerDTO.getUsername().equals(player.getUsername())) {
            Optional<Player> playerFoundByUsername = playerRepository.findByUsername(updatePlayerDTO.getUsername());
            if (playerFoundByUsername.isPresent()) {
                throw DataNotFoundException.of("PLAYER_NAME_EXISTS");
            }
            player.setUsername(updatePlayerDTO.getUsername());
        }

        // actualizar localizacion
        if (updatePlayerDTO.getLocation() != null) {
            player.setLocation(updatePlayerDTO.getLocation());
        }

        // actualizar numero1
        if (updatePlayerDTO.getPhoneNumber1() != null) {
            player.setPhoneNumber1(updatePlayerDTO.getPhoneNumber1());
        }

        playerRepository.save(player);
    }
}
