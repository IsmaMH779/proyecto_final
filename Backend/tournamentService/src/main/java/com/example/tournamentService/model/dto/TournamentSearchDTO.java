package com.example.tournamentService.model.dto;

import java.time.LocalDateTime;

public class TournamentSearchDTO {
    private Long id;
    private String name;
    private String game;
    private String organizerId;
    private String location;
    private LocalDateTime startDate;

    public TournamentSearchDTO(Long id, String name, String game, String organizerId, String location, LocalDateTime startDate) {
        this.id = id;
        this.name = name;
        this.game = game;
        this.organizerId = organizerId;
        this.location = location;
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerName) {
        this.organizerId = organizerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
}
