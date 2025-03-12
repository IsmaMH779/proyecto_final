package com.example.UserManagementService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;


@Entity
public class Player extends User{

    @Column(name = "birthdate")
    private LocalDateTime birthdate;

    public LocalDateTime getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDateTime birthdate) {
        this.birthdate = birthdate;
    }
}
