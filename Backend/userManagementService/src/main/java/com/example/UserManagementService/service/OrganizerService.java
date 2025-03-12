package com.example.UserManagementService.service;

import com.example.UserManagementService.config.DataNotFoundException;
import com.example.UserManagementService.model.Organizer;
import com.example.UserManagementService.model.Player;
import com.example.UserManagementService.repository.OrganizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizerService {
    @Autowired
    private OrganizerRepository organizerRepository;

    public Organizer getOrganizerData(long userId) {
        Optional<Organizer> organizer = organizerRepository.findById(userId);

        if (organizer.isPresent()) {
            return organizer.get();
        } else {
            throw DataNotFoundException.of("ORGANIZER_NOT_EXISTS");
        }
    }
}
