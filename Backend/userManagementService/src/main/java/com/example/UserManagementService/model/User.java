package com.example.UserManagementService.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "role", nullable = false)
    private String role;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "mail", nullable = false)
    private String mail;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "location")
    private String location;
    @Column(name = "phone_number_1")
    private String phoneNumber1;

    // getter y setter creadot por lombok
}
