package com.example.UserManagementService.config.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecklyException extends RuntimeException {
  private static final Logger log = LoggerFactory.getLogger(DecklyException.class);

  public DecklyException(String message) {
    super(message);
  }

  public static DecklyException of(String message){
    return new DecklyException(message);
  }
}