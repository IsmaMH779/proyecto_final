package com.example.tournamentService.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TournamentDTO {

    @NotNull(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser en el futuro")
    private LocalDateTime startDate;

    private String format;

    @NotNull(message = "El juego es obligatorio")
    private String game;

    @NotNull(message = "La ubicación es obligatoria")
    private String location;

    @NotNull(message = "La direccion es obligatoria")
    private String address;

    @NotNull(message = "El número máximo de jugadores es obligatorio")
    private Integer maxPlayers;
}
