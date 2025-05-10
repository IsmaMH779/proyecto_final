package com.example.tournamentService.model.dto;

import com.example.tournamentService.model.Tournament;

import java.time.LocalDateTime;

public class TournamentHistorialDTO {
    private Long id;
    private String name;
    private LocalDateTime startDate;
    private String format;
    private String game;
    private String organizerId;
    private String winnerId;
    private int playerNumbers;

    public TournamentHistorialDTO(Tournament tournament) {
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.startDate = tournament.getStartDate();
        this.format = tournament.getFormat();
        this.game = tournament.getGame();
        this.organizerId = tournament.getOrganizerId();
        this.winnerId = tournament.getWinnerId() != null ? tournament.getWinnerId().toString() : null;
        this.playerNumbers = tournament.getRegistrations().size();
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

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
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

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public int getPlayerNumbers() {
        return playerNumbers;
    }

    public void setPlayerNumbers(int playerNumbers) {
        this.playerNumbers = playerNumbers;
    }
}
