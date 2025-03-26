package com.example.authService.config.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class DeckLyException extends RuntimeException {
    private static final Logger log = LoggerFactory.getLogger(DeckLyException.class);

    public DeckLyException(String message) {
        super(message);
    }

    public static DeckLyException of(String message){
        return new DeckLyException(message);
    }
}
