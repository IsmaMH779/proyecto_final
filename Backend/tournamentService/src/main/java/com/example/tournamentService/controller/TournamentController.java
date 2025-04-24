package com.example.tournamentService.controller;

import com.example.tournamentService.config.exceptions.DataNotFoundException;
import com.example.tournamentService.model.Tournament;
import com.example.tournamentService.model.dto.TournamentDTO;
import com.example.tournamentService.model.dto.TournamentOrganizerDTO;
import com.example.tournamentService.model.dto.TournamentPlayerDTO;
import com.example.tournamentService.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@Slf4j
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    // Crear un nuevo torneo
    @PostMapping
    public ResponseEntity<?> createTournament(@RequestBody TournamentDTO tournamentDTO) {
        try {
            String organizerId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            log.info("Organizer ID: {}", organizerId);
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

    // Eliminar un jugador del torneo siendo organizador
    @DeleteMapping("/{tournamentId}/{playerId}")
    public ResponseEntity<?> removePlayerFromTournament(@PathVariable("tournamentId") long tournamentId, @PathVariable("playerId") String playerId) {
        try {
            tournamentService.removePlayerFromMyTournament(tournamentId,playerId);
            return ResponseEntity.ok().build();
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // cerrar un torneo
    @PatchMapping("/{id}/close")
    public ResponseEntity<?> closeTorunament (@PathVariable long id) {
        try {
            tournamentService.closeTournament(id);
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

    @GetMapping("/search")
    public ResponseEntity<?> searchTournaments(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String game,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        try {
            List<Tournament> tournaments =
                    tournamentService.searchTournaments(location, game, date);
            return ResponseEntity.ok(tournaments);
        } catch (DataNotFoundException e) {
            return ResponseEntity
                    .status(404)
                    .body(e.getMessage());
        }
    }

}