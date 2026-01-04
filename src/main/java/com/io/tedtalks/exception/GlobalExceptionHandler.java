package com.io.tedtalks.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler that returns standardized error responses using RFC 9457 Problem Detail.
 * Extends {@link ResponseEntityExceptionHandler} to leverage Spring's built-in exception handling
 * for common web exceptions.
 *
 * <p>This handler automatically processes standard Spring MVC exceptions (such as validation
 * errors, malformed requests, etc.) and provides custom handling for application-specific
 * exceptions.
 */
@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles {@link ResourceNotFoundException} when a requested resource cannot be found in the
   * system.
   *
   * @param ex the exception thrown when the resource is not found
   * @param request the HTTP request that triggered the exception
   * @return a {@link ProblemDetail} with 404 status and error details
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handleResourceNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {

    log.warn("Resource not found: {}", ex.getMessage());

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());

    problemDetail.setType(ProblemType.RESOURCE_NOT_FOUND.toUri());
    problemDetail.setTitle("Resource Not Found");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return problemDetail;
  }

  /**
   * Handles {@link CsvImportException} when CSV import processing encounters an error during file
   * parsing or data validation.
   *
   * @param ex the exception thrown during CSV import
   * @param request the HTTP request that triggered the exception
   * @return a {@link ProblemDetail} with 400 status and error details
   */
  @ExceptionHandler(CsvImportException.class)
  public ProblemDetail handleCsvImportException(CsvImportException ex, HttpServletRequest request) {

    log.warn("CSV import error: {}", ex.getMessage());

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());

    problemDetail.setType(ProblemType.CSV_IMPORT_ERROR.toUri());
    problemDetail.setTitle("CSV Import Error");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return problemDetail;
  }

  /**
   * Handles {@link TooManyImportsException} when the system has reached its concurrent import
   * capacity limit. Returns a 503 Service Unavailable status with retry guidance.
   *
   * @param ex the exception thrown when too many imports are running concurrently
   * @param request the HTTP request that triggered the exception
   * @return a {@link ResponseEntity} with {@link ProblemDetail} body, 503 status, and Retry-After
   *     header
   */
  @ExceptionHandler(TooManyImportsException.class)
  public ResponseEntity<ProblemDetail> handleTooManyImports(
      TooManyImportsException ex, HttpServletRequest request) {

    log.warn("Too many concurrent imports - rejecting request");

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());

    problemDetail.setType(ProblemType.TOO_MANY_IMPORTS.toUri());
    problemDetail.setTitle("Too Many Concurrent Imports");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("retryAfterSeconds", 120);

    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .header("Retry-After", "120")
        .body(problemDetail);
  }

  /**
   * Handles {@link ConstraintViolationException} when bean validation constraints are violated,
   * typically from path variables or request parameters annotated with validation constraints.
   *
   * @param ex the exception thrown when validation constraints fail
   * @param request the HTTP request that triggered the exception
   * @return a {@link ProblemDetail} with 400 status and list of constraint violations
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {

    log.warn("Constraint violation: {}", ex.getMessage());

    List<String> violations =
        ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .sorted()
            .toList();

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request parameters");

    problemDetail.setType(ProblemType.CONSTRAINT_VIOLATION.toUri());
    problemDetail.setTitle("Constraint Violation");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty("violations", violations);

    return problemDetail;
  }

  /**
   * Customizes the handling of {@link MethodArgumentNotValidException} to include detailed
   * validation error messages. This method overrides the default Spring behavior to provide a more
   * informative response with field-level validation errors.
   *
   * @param ex the exception thrown when method argument validation fails
   * @param headers the HTTP headers to be included in the response
   * @param status the HTTP status code
   * @param request the current web request
   * @return a {@link ResponseEntity} containing a {@link ProblemDetail} with validation errors
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    log.warn("Validation error: {}", ex.getMessage());

    List<String> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .sorted()
            .toList();

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request parameters");

    problemDetail.setType(ProblemType.VALIDATION_ERROR.toUri());
    problemDetail.setTitle("Validation Error");
    problemDetail.setProperty("errors", errors);

    String path = request.getDescription(false).replace("uri=", "");
    problemDetail.setInstance(URI.create(path));

    return ResponseEntity.badRequest().body(problemDetail);
  }

  /**
   * Handles all uncaught exceptions as a fallback. This method catches any exception not explicitly
   * handled by other exception handlers and returns a generic 500 Internal Server Error response.
   *
   * @param ex the exception that was not handled by specific handlers
   * @param request the HTTP request that triggered the exception
   * @return a {@link ProblemDetail} with 500 status for internal server errors
   */
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGlobalException(Exception ex, HttpServletRequest request) {

    log.error("Unexpected error occurred", ex);

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");

    problemDetail.setType(ProblemType.INTERNAL_ERROR.toUri());
    problemDetail.setTitle("Internal Server Error");
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return problemDetail;
  }
}
