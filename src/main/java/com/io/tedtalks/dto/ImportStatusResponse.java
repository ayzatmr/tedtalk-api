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

  public static ImportStatusResponse fromEntity(ImportStatusEntity entity) {
    return new ImportStatusResponse(
        entity.getImportId(),
        ImportStatus.from(entity.getStatus()),
        entity.getStartedAt(),
        entity.getCompletedAt());
  }

  public enum ImportStatus {
    PROCESSING,
    COMPLETED,
    FAILED;

    public static ImportStatus from(ImportStatusEntity.ImportStatus status) {
      return ImportStatus.valueOf(status.name());
    }
  }
}
