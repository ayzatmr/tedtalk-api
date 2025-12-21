package com.io.tedtalks.dto;

/**
 * Represents the response returned when initializing a CSV import operation.
 *
 * @param importId The unique identifier associated with the import process.
 * @param message A descriptive message about the import initialization.
 * @param statusUrl The URL to check the status of the import operation.
 */
public record ImportInitResponse(String importId, String message, String statusUrl) {
  public static ImportInitResponse of(String importId, String statusUrl) {
    return new ImportInitResponse(
        importId, "CSV import started. Check status using the provided URL.", statusUrl);
  }
}
