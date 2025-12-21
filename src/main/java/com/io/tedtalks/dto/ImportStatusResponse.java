package com.io.tedtalks.dto;

import java.time.Instant;

/**
 * Represents the status response for an import operation.
 *
 * @param importId The unique identifier of the import operation.
 * @param status The current status of the import operation.
 * @param startedAt The timestamp when the import operation was initiated.
 * @param completedAt The timestamp when the import operation was completed, if applicable.
 */
public record ImportStatusResponse(
    String importId, ImportStatus status, Instant startedAt, Instant completedAt) {

  public enum ImportStatus {
    PROCESSING,
    COMPLETED,
    FAILED;
  }
}
