package com.io.tedtalks.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.InstantSource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler is a centralized exception handling class for a Spring application. It
 * captures and processes specific exceptions and returns custom structured error responses to the
 * client using the {@link ErrorResponse} record.
 */
@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler {

  private final InstantSource instantSource;

  public GlobalExceptionHandler(InstantSource instantSource) {
    this.instantSource = instantSource;
  }

  /**
   * Handles exceptions of type {@code ResourceNotFoundException} and constructs a structured error
   * response to be returned to the client.
   *
   * @param ex the {@code ResourceNotFoundException} thrown when the requested resource is not found
   * @param request the {@code HttpServletRequest} associated with the exception at the time it
   *     occurred
   * @return a {@code ResponseEntity} containing an {@code ErrorResponse} object with details about
   *     the error
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {

    log.warn("Resource not found: {}", ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            instantSource.instant(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Handles exceptions of type {@code CsvImportException} and constructs a structured error
   * response to be returned to the client.
   *
   * @param ex the {@code CsvImportException} that occurred during the processing of a CSV import
   * @param request the {@code HttpServletRequest} associated with the exception at the time it
   *     occurred
   * @return a {@code ResponseEntity} containing an {@code ErrorResponse} object with details about
   *     the error
   */
  @ExceptionHandler(CsvImportException.class)
  public ResponseEntity<ErrorResponse> handleCsvImportException(
      CsvImportException ex, HttpServletRequest request) {

    log.warn("CSV import error: {}", ex.getMessage());

    ErrorResponse error =
        new ErrorResponse(
            instantSource.instant(),
            HttpStatus.BAD_REQUEST.value(),
            "CSV Import Error",
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.badRequest().body(error);
  }

  /**
   * Handles exceptions of type {@code MethodArgumentNotValidException} and constructs a structured
   * error response to communicate validation errors to the client.
   *
   * @param ex the {@code MethodArgumentNotValidException} thrown when request parameters fail
   *     validation
   * @param request the {@code HttpServletRequest} associated with the exception at the time it
   *     occurred
   * @return a {@code ResponseEntity} containing an {@code ErrorResponse} object with details about
   *     the validation errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<String> details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();

    ErrorResponse error =
        new ErrorResponse(
            instantSource.instant(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            "Invalid request parameters",
            request.getRequestURI(),
            details);

    return ResponseEntity.badRequest().body(error);
  }

  /**
   * Handles exceptions of type {@code HttpMessageNotReadableException}, which occur when a request
   * contains malformed JSON that cannot be deserialized.
   *
   * @param ex the {@code HttpMessageNotReadableException} thrown due to an unreadable HTTP message
   *     (e.g., malformed JSON in the request body)
   * @param request the {@code HttpServletRequest} associated with the exception at the time it
   *     occurred
   * @return a {@code ResponseEntity} containing an {@code ErrorResponse} object with details about
   *     the error
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {

    log.warn("Malformed JSON request: {}", ex.getMessage());

    String message = "Malformed JSON request";
    Throwable cause = ex.getCause();
    if (cause != null) {
      message = cause.getMessage();
    }

    ErrorResponse error =
        new ErrorResponse(
            instantSource.instant(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            request.getRequestURI());

    return ResponseEntity.badRequest().body(error);
  }

  /**
   * Handles all uncaught exceptions and constructs a structured error response to be returned to
   * the client.
   *
   * @param ex the {@code Exception} that occurred
   * @param request the {@code HttpServletRequest} associated with the exception at the time it
   *     occurred
   * @return a {@code ResponseEntity} containing an {@code ErrorResponse} object with details about
   *     the error
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      Exception ex, HttpServletRequest request) {

    log.error("Unexpected error", ex);

    ErrorResponse error =
        new ErrorResponse(
            instantSource.instant(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
