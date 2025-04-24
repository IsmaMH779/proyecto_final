package com.example.tournamentService.repository;

import com.example.tournamentService.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>, JpaSpecificationExecutor<Tournament> {
    // Optional<Tournament> findByName(String name);
    public List<Tournament> findByOrganizerId(String organizerId);

    // cerrar el torneo
    @Modifying
    @Query("UPDATE Tournament t SET t.closed = true WHERE t.id = :id")
    int markTournamentAsClosed(@Param("id") Long id);
}