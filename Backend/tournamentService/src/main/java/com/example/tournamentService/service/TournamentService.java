package com.example.tournamentService.service;

import com.example.tournamentService.config.exceptions.DataNotFoundException;
import com.example.tournamentService.model.PlayerRegistration;
import com.example.tournamentService.model.Tournament;
import com.example.tournamentService.model.dto.TournamentDTO;
import com.example.tournamentService.model.dto.TournamentOrganizerDTO;
import com.example.tournamentService.model.dto.TournamentPlayerDTO;
import com.example.tournamentService.model.dto.TournamentSearchDTO;
import com.example.tournamentService.repository.PlayerRegistrationRepository;
import com.example.tournamentService.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRegistrationRepository playerRegistrationRepository;

    public Tournament createTournament(TournamentDTO tournamentDTO, String organizerId) {
        Tournament tournament = new Tournament();

        tournament.setName(tournamentDTO.getName());
        tournament.setCreationDate(LocalDateTime.now());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setFormat(tournamentDTO.getFormat());
        tournament.setGame(tournamentDTO.getGame());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setAddress(tournamentDTO.getAddress());
        tournament.setOrganizerId(organizerId);
        tournament.setMaxPlayers(tournamentDTO.getMaxPlayers());
        tournament.setClosed(false);
        tournament.setActive(false);

        return tournamentRepository.save(tournament);
    }

    public Tournament updateTournament(Long tournamentId, TournamentDTO tournamentDTO, String organizerId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> DataNotFoundException.of("TOURNAMENT_NOT_FOUND"));

        // comprobacion de que es el mismo organizador el que hace el update
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw DataNotFoundException.of("UNAUTHORIZED_UPDATE");
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
                .orElseThrow(() -> DataNotFoundException.of("TOURNAMENT_NOT_FOUND"));

        // comprobacion de que es el mismo organizador el que hace el delete
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw DataNotFoundException.of("UNAUTHORIZED_DELETE");
        }

        tournamentRepository.delete(tournament);
    }

    // obtener torneo mediante la id
    public Tournament getTournamentsById(long id) {
        Optional<Tournament> tournament = tournamentRepository.findById(id);

        if (tournament.isPresent()) {
            return tournament.get();
        }

        throw DataNotFoundException.of("TOURNAMENT_NOT_FOUND");
    }

    // obtener torneos creados por el organizador mediante su id
    public List<TournamentOrganizerDTO> getTournamentsByOrganizer() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Tournament> tournaments = tournamentRepository.findByOrganizerId(userId);

        // extraer datos necesarios en una lista por cada torneo
        List<TournamentOrganizerDTO> tournamentOrganizerDTOS = tournaments.stream()
                .map(TournamentOrganizerDTO::new)
                .toList();

        return tournamentOrganizerDTOS;
    }

    // obtener torneos en los que el jugador esta inscrito mediante su id
    public List<TournamentPlayerDTO> getTournamentsByPlayer() {
        // Obtener el userId
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Obtener las inscripciones del jugador
        List<PlayerRegistration> listOfRegistrations = playerRegistrationRepository.findByPlayerId(userId);

        if (listOfRegistrations.isEmpty()) {
            throw new DataNotFoundException("PLAYER_NOT_REGISTERED_IN_TOURNAMENTS");
        }

        // Extraer los torneos asociados a esas inscripciones
        List<Long> tournamentIds = listOfRegistrations.stream()
                .map(registration -> registration.getTournament().getId())
                .collect(Collectors.toList());

        // Buscar los torneos por sus id
        List<Tournament> tournaments = tournamentRepository.findAllById(tournamentIds);

        // extraer datos necesarios en una lista por cada torneo
        List<TournamentPlayerDTO> tournamentPlayerDTOS = tournaments.stream()
                .map(TournamentPlayerDTO::new)
                .toList();

        if (tournaments.isEmpty()) {
            throw new DataNotFoundException("TOURNAMENTS_NOT_FOUND");
        }

        return tournamentPlayerDTOS;
    }

    // registrar un jugador mediante su id
    public void registerPlayer(Long tournamentId, String playerId) {
        // obtener el torneo
        Tournament tournament = getTournamentsById(tournamentId);
        List<PlayerRegistration> playerRegistrations = tournament.getRegistrations();

        // Verificar si el torneo ha alcanzado el máximo número de jugadores
        if (playerRegistrations.size() >= tournament.getMaxPlayers()) {
            throw new IllegalStateException("MAX_NUM_PLAYERS");
        }

        // crear la inscripción
        PlayerRegistration newPlayerRegistration = new PlayerRegistration();
        newPlayerRegistration.setTournament(tournament);
        newPlayerRegistration.setPlayerId(playerId);
        newPlayerRegistration.setRegistrationDate(LocalDateTime.now());

        // añadir la inscripción a la lista de registros del torneo
        playerRegistrations.add(newPlayerRegistration);

        // JPA guarda la inscripcion  gracias a tener CascadeType.ALL que guarda el torneo
        tournament.setRegistrations(playerRegistrations);

        // guardar el torneo (que incluirá la nueva inscripción debido a la cascada)
        tournamentRepository.save(tournament);
    }


    public void removePlayerFromMyTournament(long tournamentId, String playerId) {
        String organizerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> DataNotFoundException.of("TOURNAMENT_NOT_FOUND"));

        // comprobacion de que es el mismo organizador el que hace hace la accion
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw DataNotFoundException.of("UNAUTHORIZED_DELETE");
        }

        // eliminar la inscripcion del jugador
        List<PlayerRegistration> playerRegistrationList = tournament.getRegistrations();
        playerRegistrationList.removeIf(reg -> reg.getPlayerId().equals(playerId));

        // guardar el torneo con la lista actualizada

        tournamentRepository.save(tournament);
    }

    public void closeTournament (long tournamentId) {
        // obtener id del organizador
        String organizerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // obtener el torneo por la id
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> DataNotFoundException.of("TOURNAMENT_NOT_FOUND"));

        // comprobacion de que es el mismo organizador el que cierra el torneo
        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw DataNotFoundException.of("UNAUTHORIZED_DELETE");
        }

        tournamentRepository.markTournamentAsClosed(tournamentId);
    }

    public List<TournamentSearchDTO> searchTournaments(
            String location,
            String game,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        Specification<Tournament> spec = (root, query, cb) -> cb.conjunction();

        if (location != null && !location.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("location")), location.toLowerCase())
            );
        }
        if (game != null && !game.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("game")), game.toLowerCase())
            );
        }
        // Rango de fechas para poder buscar torneos en un mes
        if (fromDate != null) {
            LocalDateTime start = fromDate.atStartOfDay();
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("startDate"), start)
            );
        }
        if (toDate != null) {
            LocalDateTime end = toDate.plusDays(1).atStartOfDay();
            spec = spec.and((root, query, cb) ->
                    cb.lessThan(root.get("startDate"), end)
            );
        }

        List<Tournament> results = tournamentRepository.findAll(spec);
        return results.stream()
                .map(t -> new TournamentSearchDTO(
                        t.getId(),
                        t.getName(),
                        t.getGame(),
                        t.getOrganizerId(),
                        t.getLocation(),
                        t.getStartDate()
                ))
                .collect(Collectors.toList());
    }
}
