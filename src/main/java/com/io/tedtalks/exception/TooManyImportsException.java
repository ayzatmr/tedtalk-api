package com.io.tedtalks.exception;

/**
 * Exception thrown when the system cannot accept more import requests due to capacity constraints.
 */
public class TooManyImportsException extends RuntimeException {
  public TooManyImportsException(String message) {
    super(message);
  }
}
