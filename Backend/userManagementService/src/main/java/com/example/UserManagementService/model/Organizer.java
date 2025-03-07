package com.example.UserManagementService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Organizer extends User {
    @Column(name="adress", nullable = false)
    private String adress;
    @Column(name="description")
    private String description;
    @Column(name = "phone_number_2")
    private String phoneNumber2;

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }
}
