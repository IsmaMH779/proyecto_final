package com.example.UserManagementService.model.dto.update;

import jakarta.validation.constraints.Pattern;

public class UpdatePlayerDTO {
    private String username;
    private String location;
    private String phoneNumber1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
