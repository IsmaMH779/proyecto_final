package com.example.UserManagementService.service;

import com.example.UserManagementService.config.exceptions.DataNotFoundException;
import com.example.UserManagementService.model.Organizer;
import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.model.dto.register.OrganizerRegisterDTO;
import com.example.UserManagementService.model.dto.update.UpdateOrganizerDTO;
import com.example.UserManagementService.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrganizerService {
    @Autowired
    private OrganizerRepository organizerRepository;

    public void saveOrganizer(OrganizerRegisterDTO organizerRegisterDTO, long userId) {
        Organizer organizer = new Organizer();

        organizer.setId(userId);
        organizer.setRole("Organizer");
        organizer.setUsername(organizerRegisterDTO.getUsername());
        organizer.setMail(organizerRegisterDTO.getMail());
        organizer.setLocation(organizerRegisterDTO.getLocation());
        organizer.setAdress(organizerRegisterDTO.getAdress());

        organizerRepository.save(organizer);
    }

    public Organizer getOrganizerData(long userId) {
        Optional<Organizer> organizer = organizerRepository.findById(userId);

        if (organizer.isPresent()) {
            return organizer.get();
        } else {
            throw DataNotFoundException.of("ORGANIZER_NOT_EXISTS");
        }
    }

    public void updateOrganizerData(long userId, UpdateOrganizerDTO updateOrganizerDTO) {
        //obtener el organizador
        Organizer organizer = getOrganizerData(userId);

        // acutalizar username
        if (updateOrganizerDTO.getUsername() != null && !updateOrganizerDTO.getUsername().equals(organizer.getUsername())) {
            Optional<Organizer> organizerFoundByUsername = organizerRepository.findByUsername(updateOrganizerDTO.getUsername());
            if (organizerFoundByUsername.isPresent()) {
                throw DataNotFoundException.of("ORGANIZER_NAME_EXISTS");
            }
            organizer.setUsername(updateOrganizerDTO.getUsername());
        }

        // actualizar localizacion
        if (updateOrganizerDTO.getLocation() != null) {
            organizer.setLocation(updateOrganizerDTO.getLocation());
        }

        // actualizar direccion
        if (updateOrganizerDTO.getAddress() != null) {
            organizer.setAdress(updateOrganizerDTO.getAddress());
        }

        // actualizar description
        if (updateOrganizerDTO.getDescription() != null) {
            organizer.setDescription(updateOrganizerDTO.getDescription());
        }

        // actualizar numero1
        if (updateOrganizerDTO.getPhoneNumber1() != null) {
            organizer.setPhoneNumber1(updateOrganizerDTO.getPhoneNumber1());
        }

        // actualizar numero2
        if (updateOrganizerDTO.getPhoneNumber2() != null) {
            organizer.setPhoneNumber2(updateOrganizerDTO.getPhoneNumber2());
        }

        organizerRepository.save(organizer);
    }

    // guardar la imagen de perfil
    public String updateProfileImage(MultipartFile file) throws IOException {
        long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Validación de tipo
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("INVALID_FILE_TYPE");
        }

        // Obtener el organizer y la imagen anterior
        Organizer organizer = getOrganizerData(userId);
        String oldImage = organizer.getImageUrl();

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

        // Actualizar imagen del organizer
        organizer.setImageUrl(fileName);
        organizerRepository.save(organizer);

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
