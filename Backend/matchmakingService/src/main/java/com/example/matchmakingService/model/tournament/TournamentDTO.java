package com.example.matchmakingService.model.tournament;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TournamentDTO {
    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private LocalDateTime startDate;
    private String format;
    private String game;
    private String location;
    private String address;
    private String organizerId;
    private int maxPlayers;
    private boolean closed;
    private boolean active;
    private List<PlayerRegistration> registrations = new ArrayList<>();

    public TournamentDTO() {
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    /*
    public String getOrganizerName() { return organizerName; }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

     */

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
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

    public List<PlayerRegistration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<PlayerRegistration> registrations) {
        this.registrations = registrations;
    }
}
