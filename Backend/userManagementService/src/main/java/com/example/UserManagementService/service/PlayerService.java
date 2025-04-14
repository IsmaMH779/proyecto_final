package com.example.UserManagementService.service;

import com.example.UserManagementService.config.exceptions.DataNotFoundException;
import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.model.dto.update.UpdatePlayerDTO;
import com.example.UserManagementService.repository.PlayerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    //ruta donde se guardan las imagenes de perfil
    @Value("${assets.profileImage}")
    String uploadDir;


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

    // guardar la imagen de perfil
    public String updateProfileImage(MultipartFile file) throws IOException {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Validación de tipo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("INVALID_FILE_TYPE");
        }

        // Obtener el jugador y la imagen anterior
        Player player = getPlayerData(userId);
        String oldImage = player.getImageUrl();

        // Carpeta donde guardar (un nivel fuera del proyecto, como pediste)
        String uploadDir = "../uploads/profile_pics/";
        Files.createDirectories(Paths.get(uploadDir));

        // Nombre aleatorio para el archivo
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Leer la imagen
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) throw new IOException("INVALID_IMAGE");

        // Guardar como JPEG
        try (OutputStream os = Files.newOutputStream(filePath)) {
            ImageIO.write(originalImage, "jpg", os);
        }

        // Actualizar imagen del jugador
        player.setImageUrl(fileName);
        playerRepository.save(player);

        // Borrar imagen anterior si existe
        if (oldImage != null) {
            Path oldFilePath = Paths.get(uploadDir, oldImage);
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        }

        return fileName;
    }


}
