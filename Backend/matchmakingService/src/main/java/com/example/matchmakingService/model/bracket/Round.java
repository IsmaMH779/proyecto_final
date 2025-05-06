package com.example.matchmakingService.model.bracket;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class Round {
    @Field(write = Field.Write.ALWAYS)
    private List<Game> games;

    public Round(List<Game> games) {
        this.games = games;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }
}
