package com.example.UserManagementService.repository;

import com.example.UserManagementService.model.Organizer;
import com.example.UserManagementService.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    Optional<Organizer> findByUsername(String username);

}