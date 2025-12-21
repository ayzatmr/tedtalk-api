package com.io.tedtalks.exception;

import java.time.Instant;
import java.util.List;

/**
 * Represents a structured response body for returning error details in the context of HTTP
 * exceptions. This record is primarily used to standardize the communication of errors to the
 * client.
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    List<String> details) {

  public ErrorResponse(Instant timestamp, int status, String error, String message, String path) {
    this(timestamp, status, error, message, path, null);
  }
}
