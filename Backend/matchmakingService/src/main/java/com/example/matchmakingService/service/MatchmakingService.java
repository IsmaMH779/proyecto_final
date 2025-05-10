package com.example.matchmakingService.service;

import com.example.matchmakingService.config.exceptions.DeckLyException;
import com.example.matchmakingService.model.bracket.Game;
import com.example.matchmakingService.model.bracket.Player;
import com.example.matchmakingService.model.bracket.Round;
import com.example.matchmakingService.model.bracket.Tournament;
import com.example.matchmakingService.model.tournament.PlayerRegistration;
import com.example.matchmakingService.model.tournament.TournamentDTO;
import com.example.matchmakingService.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MatchmakingService {
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Genera la estructura inicial del torneo.
     */
    public void generateTournamentStructure(TournamentDTO tournamentDTO) {
        List<Round> rounds = new ArrayList<>();

        // Primera ronda (puede incluir byes)
        List<Game> firstGames = createFirstRound(tournamentDTO);
        rounds.add(new Round(firstGames));

        // Calcular número total de rondas
        int totalRounds = (int) Math.ceil(Math.log(firstGames.size() * 2) / Math.log(2));

        // Rondas posteriores
        for (int i = 1; i < totalRounds; i++) {
            int numGames = (int) Math.pow(2, totalRounds - i - 1);
            List<Game> nextGames = new ArrayList<>();
            for (int j = 0; j < numGames; j++) {
                nextGames.add(new Game(null, null));
            }
            rounds.add(new Round(nextGames));
        }

        Tournament tournament = new Tournament(
                tournamentDTO.getId(),
                tournamentDTO.getOrganizerId(),
                tournamentDTO.getName(),
                tournamentDTO.isActive(),
                tournamentDTO.isClosed(),
                rounds
        );

        tournamentRepository.save(tournament);

        // Emitir actualización al canal WebSocket específico
        messagingTemplate.convertAndSend("/topic/tournament/" + tournament.getId(), tournament);
    }

    private List<Game> createFirstRound(TournamentDTO tournamentDTO) {
        List<PlayerRegistration> players = tournamentDTO.getRegistrations();
        Collections.shuffle(players);
        List<Game> games = new ArrayList<>();
        int i = 0;
        while (i < players.size()) {
            Player p1 = new Player(Long.parseLong(players.get(i).getPlayerId()), null);
            Player p2 = null;
            if (i + 1 < players.size()) {
                p2 = new Player(Long.parseLong(players.get(i + 1).getPlayerId()), null);
            }
            games.add(new Game(p1, p2));
            i += 2;
        }
        return games;
    }

    /**
     * Avanza un jugador identificado por playerId en el torneo tournamentId.
     * Busca en la ronda más reciente donde participa, marca al jugador como winner,
     * y lo coloca en la siguiente ronda en la posición nula.
     */
    public void advancePlayer(String tournamentId, Long playerId) {
        Optional<Tournament> opt = tournamentRepository.findById(Long.parseLong(tournamentId));
        if (!opt.isPresent()) {
            throw new IllegalArgumentException("Torneo no encontrado: " + tournamentId);
        }
        Tournament tournament = opt.get();
        List<Round> rounds = tournament.getRounds();
        int lastRoundIndex = rounds.size() - 1;

        // Buscar la ronda más reciente donde aparece el jugador iterando guardando el ultimo indice donde se encuentra el jugador
        int roundIndex = -1;
        int gameIndex = -1;
        for (int i = 0; i < rounds.size(); i++) {
            List<Game> games = rounds.get(i).getGames();
            for (int j = 0; j < games.size(); j++) {
                Game game = games.get(j);
                if ((game.getPlayer1() != null && game.getPlayer1().getPlayerId().equals(playerId))
                        || (game.getPlayer2() != null && game.getPlayer2().getPlayerId().equals(playerId))) {
                    roundIndex = i;
                    gameIndex = j;
                }
            }
        }

        if (roundIndex == -1) {
            throw new IllegalArgumentException("Jugador no encontrado en ninguna ronda: " + playerId);
        }

        // Marcar winner en la ronda actual
        Round currentRound = rounds.get(roundIndex);
        Game currentGame = currentRound.getGames().get(gameIndex);

        Player player = null;

        if (currentGame.getPlayer1() != null && currentGame.getPlayer1().getPlayerId().equals(playerId)) {
            currentGame.getPlayer1().setWinner(true);
            currentGame.getPlayer2().setWinner(false);

            player = currentGame.getPlayer1();
        } else if (currentGame.getPlayer2() != null && currentGame.getPlayer2().getPlayerId().equals(playerId)) {
            currentGame.getPlayer2().setWinner(true);
            currentGame.getPlayer1().setWinner(false);

            player = currentGame.getPlayer2();
        }

        // Si no es la última ronda, ubicar al jugador en la siguiente ronda
        if (roundIndex < lastRoundIndex) {
            Round nextRound = rounds.get(roundIndex + 1);
            List<Game> nextGames = nextRound.getGames();
            int targetIndex = gameIndex / 2;
            Game nextGame = nextGames.get(targetIndex);

            if (nextGame.getPlayer1() == null) {
                Player playerTemplate = new Player(playerId,null);

                playerTemplate.setWinner(null);

                nextGame.setPlayer1(playerTemplate);
            } else if (nextGame.getPlayer2() == null) {
                Player playerTemplate = new Player(playerId,null);

                playerTemplate.setWinner(null);

                nextGame.setPlayer2(playerTemplate);
            } else {
                throw new IllegalStateException("Ambas posiciones ocupadas en la siguiente ronda");
            }
        }

        // Guardar cambios
        tournamentRepository.save(tournament);

        // Emitir actualización al canal WebSocket
       messagingTemplate.convertAndSend("/topic/tournament/" + tournament.getId(), tournament);
    }

    public Object getTournament(String tournamentID) {
        return tournamentRepository.findById(Long.parseLong(tournamentID))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Torneo no encontrado"));
    }

    public void closeTournament(Long id) {
        Optional<Tournament> op = tournamentRepository.findById(id);

        if (op.isPresent()) {
            Tournament tournament = op.get();
            tournament.setClosed(true);
            tournament.setActive(false);
            tournamentRepository.save(tournament);

            // Emitir actualización al canal WebSocket
            messagingTemplate.convertAndSend("/topic/tournament/" + tournament.getId(), tournament);

            return;
        }

        throw new DeckLyException("TOURNAMENT_NOT_FOUND");
    }

}