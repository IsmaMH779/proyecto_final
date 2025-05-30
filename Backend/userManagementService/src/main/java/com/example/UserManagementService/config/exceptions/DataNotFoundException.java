package com.example.UserManagementService.config.exceptions;

public class DataNotFoundException extends DecklyException {

    public DataNotFoundException(String message) {
        super(message);
    }

    public static DataNotFoundException of(String message) {
        return new DataNotFoundException (message);
    }
}
