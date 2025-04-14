package com.example.tournamentService.service;

import com.example.tournamentService.config.exceptions.DataNotFoundException;
import com.example.tournamentService.model.Tournament;
import com.example.tournamentService.model.dto.TournamentDTO;
import com.example.tournamentService.repository.PlayerRegistrationRepository;
import com.example.tournamentService.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.List;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    PlayerRegistrationRepository playerRegistrationRepository;

    public Tournament createTournament(TournamentDTO tournamentDTO, String organizerId) {
        Tournament tournament = new Tournament();

        tournament.setName(tournamentDTO.getName());
        tournament.setCreationDate(LocalDate.now());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setFormat(tournamentDTO.getFormat());
        tournament.setGame(tournamentDTO.getGame());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setOrganizerId(organizerId);
        tournament.setMaxPlayers(tournamentDTO.getMaxPlayers());

        return tournamentRepository.save(tournament);
    }

    public Tournament updateTournament(Long tournamentId, TournamentDTO tournamentDTO, String organizerId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> DataNotFoundException.of("Torneo no encontrado"));

        // comprobacion de que es el mismo organizador el que hace el update
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw DataNotFoundException.of("Intento no autorizado para actualizar el torneo");
        }

        tournament.setName(tournamentDTO.getName());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setFormat(tournamentDTO.getFormat());
        tournament.setGame(tournamentDTO.getGame());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setMaxPlayers(tournamentDTO.getMaxPlayers());

        return tournamentRepository.save(tournament);
    }

    public void deleteTournament(Long tournamentId, String organizerId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> DataNotFoundException.of("Torneo no encontrado"));

        // comprobacion de que es el mismo organizador el que hace el delete
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw DataNotFoundException.of("Intento no autorizado para eliminar el torneo");
        }

        tournamentRepository.delete(tournament);
    }

    public List<Tournament> getTournamentsByOrganizer(String organizerId) {
        return null;
    }

    public List<Tournament> getTournamentsByPlayer(String playerId) {
        return null;
    }

    public Tournament registerPlayer(Long tournamentId, String playerId) {
        return null;
    }

    public List<Tournament> searchTournaments() {
        return null;
    }
}
