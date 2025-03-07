package com.example.UserManagementService.model;

import jakarta.persistence.*;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }
}
