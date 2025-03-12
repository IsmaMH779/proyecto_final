package com.example.UserManagementService.config;

public class DataNotFoundException extends DeckLyException{

    public DataNotFoundException(String message) {
        super(message);
    }

    public static DataNotFoundException of(String message) {
        return new DataNotFoundException (message);
    }
}
