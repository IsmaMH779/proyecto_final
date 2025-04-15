package com.example.tournamentService.model.dto;

import com.example.tournamentService.model.Tournament;

import java.time.LocalDate;

public class TournamentOrganizerDTO {
    private long id;
    private LocalDate startDate;
    private String name;

    public TournamentOrganizerDTO(Tournament tournament) {
        this.id = tournament.getId();
        this.startDate = tournament.getStartDate();
        this.name = tournament.getName();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
