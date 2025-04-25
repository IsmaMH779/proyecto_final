package com.example.tournamentService.model.dto;

import java.time.LocalDateTime;

public class TournamentSearchDTO {
    private String name;
    private String game;
    private String organizerName;
    private String location;
    private LocalDateTime startDate;

    public TournamentSearchDTO(String name, String game, String organizerName, String location, LocalDateTime startDate) {
        this.name = name;
        this.game = game;
        this.organizerName = organizerName;
        this.location = location;
        this.startDate = startDate;
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

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
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
