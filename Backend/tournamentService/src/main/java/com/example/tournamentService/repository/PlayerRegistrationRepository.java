package com.example.tournamentService.repository;

import com.example.tournamentService.model.PlayerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlayerRegistrationRepository extends JpaRepository<PlayerRegistration, Long> {
    // List<PlayerRegistration> findByTournamentId(Long tournamentId);
    List<PlayerRegistration> findByPlayerId(String playerId);

    // Conteo de inscripciones cuyos torneos tienen startDate en un rango
    @Query("SELECT COUNT(pr) FROM PlayerRegistration pr " +
            "WHERE pr.tournament.startDate BETWEEN :from AND :to")
    long countByTournamentStartDateBetween(@Param("from") LocalDateTime from,
                                           @Param("to")   LocalDateTime to);
}
