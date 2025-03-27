package com.example.tournamentService.repository;

import com.example.tournamentService.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    // Optional<Tournament> findByName(String name);
}
