package com.example.matchmakingService.model.bracket;

import org.springframework.data.mongodb.core.mapping.Field;

public class Player {
    @Field("playerId")
    private Long playerId;
    @Field("winner")
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

    public Boolean isWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }
}
