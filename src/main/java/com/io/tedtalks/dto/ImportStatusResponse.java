package com.io.tedtalks.dto;

import com.io.tedtalks.model.ImportStatusModel;
import java.time.Instant;
import java.time.ZoneOffset;

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

  public static ImportStatusResponse fromEntity(ImportStatusModel entity) {
    return new ImportStatusResponse(
        entity.getImportId(),
        ImportStatus.valueOf(entity.getStatus()),
        entity.getStartedAt() != null ? entity.getStartedAt().toInstant(ZoneOffset.UTC) : null,
        entity.getCompletedAt() != null ? entity.getCompletedAt().toInstant(ZoneOffset.UTC) : null);
  }

  /** Represents the status of an import process. */
  public enum ImportStatus {
    PROCESSING,
    COMPLETED,
    FAILED
  }
}
