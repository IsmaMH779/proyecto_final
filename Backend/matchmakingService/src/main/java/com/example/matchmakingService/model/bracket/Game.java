package com.example.matchmakingService.model.bracket;

import org.springframework.data.mongodb.core.mapping.Field;

public class Game {
    @Field(write = Field.Write.ALWAYS)
    private Player player1;
    @Field(write = Field.Write.ALWAYS)
    private Player player2;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
}
