package com.example.userManagementService.repository;

import com.example.userManagementService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}