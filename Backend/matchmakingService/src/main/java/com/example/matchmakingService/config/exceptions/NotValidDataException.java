package com.example.matchmakingService.config.exceptions;

public class NotValidDataException extends DeckLyException {
    public NotValidDataException(String message) {
        super(message);
    }

    public static NotValidDataException of(String message) {
        return new NotValidDataException (message);
    }
}
