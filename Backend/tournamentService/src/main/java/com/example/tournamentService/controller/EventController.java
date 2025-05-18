package com.example.tournamentService.controller;

import com.example.tournamentService.model.Event;
import com.example.tournamentService.model.dto.EventDTO;
import com.example.tournamentService.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Event>> list() {
        return ResponseEntity.ok(service.getMyEvents());
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody EventDTO dto) {
        Event saved = service.createEvent(dto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id,
                                        @RequestBody EventDTO dto) {
        Event updated = service.updateEvent(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<Event>> weekly() {
        return ResponseEntity.ok(service.getWeeklyEvents());
    }
}
