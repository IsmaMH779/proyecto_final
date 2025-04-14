package com.example.tournamentService.service;

import com.example.tournamentService.model.Tournament;
import com.example.tournamentService.model.dto.TournamentDTO;
import com.example.tournamentService.repository.PlayerRegistrationRepository;
import com.example.tournamentService.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    PlayerRegistrationRepository playerRegistrationRepository;

    public Tournament createTournament(TournamentDTO tournamentDTO, String organizerId) {

    }

    public Tournament updateTournament(Long tournamentId, TournamentDTO tournamentDTO, String organizerId) {

    }

    public void deleteTournament(Long tournamentId, String organizerId) {

    }

    public List<Tournament> getTournamentsByOrganizer(String organizerId) {

    }

    public List<Tournament> getTournamentsByPlayer(String playerId) {

    }

    public Tournament registerPlayer(Long tournamentId, String playerId) {

    }

    public List<Tournament> searchTournaments() {

    }
}
