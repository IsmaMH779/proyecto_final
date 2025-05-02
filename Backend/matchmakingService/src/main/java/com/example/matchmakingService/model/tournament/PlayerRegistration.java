package com.example.matchmakingService.model.tournament;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;


public class PlayerRegistration {
    private Long id;
    @JsonBackReference
    private TournamentDTO tournamentDTO;
    private String playerId;
    private LocalDateTime registrationDate;

    public PlayerRegistration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TournamentDTO getTournament() {
        return tournamentDTO;
    }

    public void setTournament(TournamentDTO tournamentDTO) {
        this.tournamentDTO = tournamentDTO;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}
