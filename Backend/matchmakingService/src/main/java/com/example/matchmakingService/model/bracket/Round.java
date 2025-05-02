package com.example.matchmakingService.model.bracket;

import java.util.List;

public class Round {
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
