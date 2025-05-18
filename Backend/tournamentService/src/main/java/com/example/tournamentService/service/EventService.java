package com.example.tournamentService.service;

import com.example.tournamentService.config.exceptions.DataNotFoundException;
import com.example.tournamentService.model.Event;
import com.example.tournamentService.model.dto.EventDTO;
import com.example.tournamentService.repository.EventRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public Event createEvent(EventDTO dto) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal().toString();
        Event e = new Event();
        e.setTitle(dto.getTitle());
        e.setDescription(dto.getDescription());
        e.setEventTime(dto.getEventTime());
        e.setUserId(userId);
        return repo.save(e);
    }

    public List<Event> getMyEvents() {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal().toString();
        return repo.findByUserId(userId);
    }

    public Event updateEvent(Long id, EventDTO dto) {
        Event existing = repo.findById(id)
                .orElseThrow(() -> DataNotFoundException.of("EVENT_NOT_FOUND"));
        String userId = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal().toString();
        if (!existing.getUserId().equals(userId)) {
            throw DataNotFoundException.of("UNAUTHORIZED_UPDATE");
        }
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setEventTime(dto.getEventTime());
        return repo.save(existing);
    }

    public void deleteEvent(Long id) {
        Event existing = repo.findById(id)
                .orElseThrow(() -> DataNotFoundException.of("EVENT_NOT_FOUND"));
        String userId = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal().toString();
        if (!existing.getUserId().equals(userId)) {
            throw DataNotFoundException.of("UNAUTHORIZED_DELETE");
        }
        repo.delete(existing);
    }

    public List<Event> getWeeklyEvents() {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime to = today.with(DayOfWeek.SUNDAY).atTime(23,59,59);
        return repo.findByEventTimeBetween(from, to);
    }
}
