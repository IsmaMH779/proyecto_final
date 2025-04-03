package com.example.UserManagementService.service;

import com.example.UserManagementService.config.exceptions.DataNotFoundException;
import com.example.UserManagementService.model.Organizer;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.model.dto.update.UpdateOrganizerDTO;
import com.example.UserManagementService.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizerService {
    @Autowired
    private OrganizerRepository organizerRepository;

    public void saveOrganizer(PlayerRegisterDTO playerRegisterDTO, long userId) {
        Organizer organizer = new Organizer();

        organizer.setId(userId);
        organizer.setRole("Organizer");
        organizer.setUsername(playerRegisterDTO.getUsername());
        organizer.setMail(playerRegisterDTO.getMail());
        organizer.setLocation(playerRegisterDTO.getLocation());
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


}
