package com.example.matchmakingService.controller;

import com.example.matchmakingService.model.bracket.Tournament;
import com.example.matchmakingService.service.MatchmakingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TournamentWebSocketController {

    @Autowired
    private MatchmakingService matchmakingService;

    // Permite a los clientes solicitar datos del torneo a través de WebSocket
    @MessageMapping("/tournament/{tournamentId}")
    @SendTo("/topic/tournament/{tournamentId}")
    public Tournament getTournamentUpdates(@DestinationVariable String tournamentId) {
        return (Tournament) matchmakingService.getTournament(tournamentId);
    }
}