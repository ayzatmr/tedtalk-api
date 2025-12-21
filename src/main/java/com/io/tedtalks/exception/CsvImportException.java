package com.io.tedtalks.exception;

/**
 * CsvImportException is a custom exception that signals errors during the process of importing data
 * from a CSV file into the application.
 */
public final class CsvImportException extends RuntimeException {
  public CsvImportException(String message) {
    super(message);
  }

  public CsvImportException(String message, Throwable cause) {
    super(message, cause);
  }
}
