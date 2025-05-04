package com.example.matchmakingService.controller;

import com.example.matchmakingService.model.tournament.TournamentDTO;
import com.example.matchmakingService.service.MatchmakingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matchmaking")
public class MatchmakingController {

    @Autowired
    private MatchmakingService matchmakingService;

    @PostMapping("/start")
    public ResponseEntity<?> startMatchmaking(@RequestBody TournamentDTO tournamentDTO) {
        matchmakingService.generateTournamentStructure(tournamentDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/advance/{tournamentId}/{playerId}")
    public ResponseEntity<?> advancePlayer(@PathVariable("tournamentId") String tournamentId, @PathVariable("playerId") Long playerId) {
        try {
            matchmakingService.advancePlayer(tournamentId, playerId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
