package com.example.matchmakingService.model.bracket;

public class Player {
    private Long playerId;
    private Boolean winner;

    public Player(Long playerId, Boolean winner) {
        this.playerId = playerId;
        this.winner = winner;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}
