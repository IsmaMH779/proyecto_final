package com.example.tournamentService.repository;

import com.example.tournamentService.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserId(String userId);
    List<Event> findByEventTimeBetween(LocalDateTime from, LocalDateTime to);
}
