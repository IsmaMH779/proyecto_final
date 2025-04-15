package com.example.tournamentService.repository;

import com.example.tournamentService.model.PlayerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRegistrationRepository extends JpaRepository<PlayerRegistration, Long> {
    // List<PlayerRegistration> findByTournamentId(Long tournamentId);
    List<PlayerRegistration> findByPlayerId(String playerId);
}
