package com.example.tournamentService.controller;

import com.example.tournamentService.model.Tournament;
import com.example.tournamentService.model.dto.TournamentDTO;
import com.example.tournamentService.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody TournamentDTO tournamentDTO) {
        String organizerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Tournament tournament = tournamentService.createTournament(tournamentDTO, organizerId);
        return ResponseEntity.ok(tournament);
    }
}
