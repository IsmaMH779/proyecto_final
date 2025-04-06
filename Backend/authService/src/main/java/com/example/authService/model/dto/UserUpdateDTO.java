package com.example.authService.model.dto;

public class UserUpdateDTO {
    private String username;

    public UserUpdateDTO(String username, String email) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
