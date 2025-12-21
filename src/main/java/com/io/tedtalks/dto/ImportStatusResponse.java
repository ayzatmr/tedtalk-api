package com.io.tedtalks.dto;

import com.io.tedtalks.entity.ImportStatusEntity;
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

  /**
   * Converts an {@code ImportStatusEntity} to an {@code ImportStatusResponse}.
   *
   * @param entity the {@code ImportStatusEntity} instance to be converted
   * @return a new instance of {@code ImportStatusResponse} constructed from the given entity
   */
  public static ImportStatusResponse fromEntity(ImportStatusEntity entity) {
    return new ImportStatusResponse(
        entity.getImportId(),
        ImportStatus.valueOf(entity.getStatus().name()),
        entity.getStartedAt(),
        entity.getCompletedAt());
  }

  /** Represents the status of an import process. */
  public enum ImportStatus {
    PROCESSING,
    COMPLETED,
    FAILED
  }
}
