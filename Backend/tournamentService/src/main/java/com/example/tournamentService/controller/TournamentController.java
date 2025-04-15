package com.example.tournamentService.controller;

import com.example.tournamentService.config.exceptions.DataNotFoundException;
import com.example.tournamentService.model.Tournament;
import com.example.tournamentService.model.dto.TournamentDTO;
import com.example.tournamentService.model.dto.TournamentOrganizerDTO;
import com.example.tournamentService.model.dto.TournamentPlayerDTO;
import com.example.tournamentService.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // Crear un nuevo torneo
    @PostMapping
    public ResponseEntity<?> createTournament(@RequestBody TournamentDTO tournamentDTO) {
        try {
            String organizerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            Tournament tournament = tournamentService.createTournament(tournamentDTO, organizerId);
            return ResponseEntity.ok(tournament);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar un torneo
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTournament(@PathVariable("id") Long tournamentId, @RequestBody TournamentDTO tournamentDTO) {
        try {
            String organizerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            Tournament tournament = tournamentService.updateTournament(tournamentId, tournamentDTO, organizerId);
            return ResponseEntity.ok(tournament);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // Eliminar un torneo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTournament(@PathVariable("id") Long tournamentId) {
        try {
            String organizerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            tournamentService.deleteTournament(tournamentId, organizerId);
            return ResponseEntity.ok().build();
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener un torneo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTournamentByID(@PathVariable long id) {
        try {
            Tournament tournament = tournamentService.getTournamentsById(id);
            return ResponseEntity.ok(tournament);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener torneos organizados por el usuario autenticado
    @GetMapping("/organizer")
    public ResponseEntity<?> getOrganizerTournaments() {
        List<TournamentOrganizerDTO> tournamentDataList = tournamentService.getTournamentsByOrganizer();
        return ResponseEntity.ok(tournamentDataList);
    }

    // Obtener torneos donde el jugador autenticado está registrado
    @GetMapping("/player")
    public ResponseEntity<?> getPlayerTournaments() {
        try {
            List<TournamentPlayerDTO> tournamentDataList = tournamentService.getTournamentsByPlayer();
            return ResponseEntity.ok(tournamentDataList);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Registrar al usuario autenticado en un torneo específico
    @PostMapping("/{tournamentID}/register")
    public ResponseEntity<?> registerPlayerOnTournament(@PathVariable Long tournamentID) {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            tournamentService.registerPlayer(tournamentID, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}