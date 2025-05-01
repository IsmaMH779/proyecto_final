package com.example.tournamentService.model.dto;

import com.example.tournamentService.model.Tournament;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TournamentPlayerDTO {
    private long id;
    private LocalDateTime startDate;
    private String name;
    private boolean closed;
    private boolean active;
    private String format;
    private String game;
    private String location;

    public TournamentPlayerDTO(Tournament tournament) {
        this.id = tournament.getId();
        this.startDate = tournament.getStartDate();
        this.name = tournament.getName();
        this.active = tournament.isActive();
        this.format = tournament.getFormat();
        this.game = tournament.getGame();
        this.location = tournament.getLocation();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
