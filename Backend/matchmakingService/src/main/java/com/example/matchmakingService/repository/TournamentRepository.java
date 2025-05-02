package com.example.matchmakingService.repository;

import com.example.matchmakingService.model.bracket.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentRepository extends MongoRepository<Tournament,String> {
}
