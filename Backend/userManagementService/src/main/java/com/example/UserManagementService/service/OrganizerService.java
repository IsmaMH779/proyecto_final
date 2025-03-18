package com.example.UserManagementService.service;

import com.example.UserManagementService.config.DataNotFoundException;
import com.example.UserManagementService.model.Organizer;
import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.model.dto.register.PlayerRegisterDTO;
import com.example.UserManagementService.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizerService {
    @Autowired
    private OrganizerRepository organizerRepository;

    public static void saveOrganizer(PlayerRegisterDTO playerRegisterDTO, long userId) {
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

}
