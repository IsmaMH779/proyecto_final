package com.example.UserManagementService.config.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeckLyException extends RuntimeException {
  private static final Logger log = LoggerFactory.getLogger(DeckLyException.class);

  public DeckLyException(String message) {
    super(message);
  }

  public static DeckLyException of(String message){
    return new DeckLyException(message);
  }
}