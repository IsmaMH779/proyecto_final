package com.example.matchmakingService.model.bracket;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "tournament")
public class Tournament {
    @Id
    private Long id;
    private String organizerId;
    private String name;
    private Boolean active;
    private Boolean closed;

    @Field(write = Field.Write.ALWAYS)
    private List<Round> rounds;

    public Tournament(Long id, String organizerId, String name, boolean active, boolean closed, List<Round> rounds) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.active = active;
        this.closed = closed;
        this.rounds = rounds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }
}
