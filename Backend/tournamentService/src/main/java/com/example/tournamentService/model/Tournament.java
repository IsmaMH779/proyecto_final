package com.example.tournamentService.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tournament")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "format", nullable = true) // El formato puede ser nulo
    private String format;

    @Column(name = "game", nullable = false)
    private String game;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "id_organizer", nullable = false)
    private String organizerId;

    @Column(name = "max_players", nullable = false)
    private String maxPlayers;

    // Relación con los jugadores (inscripciones)
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerRegistration> registrations = new ArrayList<>();

    public Tournament() {
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(String maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<PlayerRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<PlayerRegistration> registrations) {
        this.registrations = registrations;
    }
}
