package com.example.tournamentService.repository;

import com.example.tournamentService.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    // Optional<Tournament> findByName(String name);
    public List<Tournament> findByOrganizerId(long organizerId);
}
