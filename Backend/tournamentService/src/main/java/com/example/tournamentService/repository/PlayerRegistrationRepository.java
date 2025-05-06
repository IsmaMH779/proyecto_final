package com.example.tournamentService.repository;

import com.example.tournamentService.model.PlayerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRegistrationRepository extends JpaRepository<PlayerRegistration, Long> {
    // List<PlayerRegistration> findByTournamentId(Long tournamentId);
    List<PlayerRegistration> findByPlayerId(String playerId);

    // Conteo total de inscripciones (jugadores registrados en todos los torneos)
    @Query("SELECT COUNT(pr) FROM PlayerRegistration pr")
    long countAllRegistrations();
}
