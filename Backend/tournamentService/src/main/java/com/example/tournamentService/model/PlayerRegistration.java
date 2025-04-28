package com.example.tournamentService.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class PlayerRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscription")
    private Long id;

    // Relación obligatoria con Tournament y con anotaciones para evitar recursividad
    @ManyToOne
    @JoinColumn(name = "id_tournament", nullable = false)
    @JsonBackReference
    private Tournament tournament;

    @Column(name = "id_player", nullable = false)
    private String playerId;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    public PlayerRegistration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
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
