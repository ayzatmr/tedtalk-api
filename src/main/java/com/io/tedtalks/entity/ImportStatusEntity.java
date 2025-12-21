package com.io.tedtalks.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.InstantSource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the status of an import process tracked in the system. */
@Entity
@Table(name = "import_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImportStatusEntity {

  @Id private String importId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ImportStatus status;

  @Column(nullable = false, updatable = false)
  private Instant startedAt;

  private Instant completedAt;

  @Version private long version;

  private ImportStatusEntity(String importId, Instant startedAt) {
    this.importId = importId;
    this.status = ImportStatus.PROCESSING;
    this.startedAt = startedAt;
  }

  /**
   * Creates and returns a new {@code ImportStatusEntity} instance with the given import ID and the
   * current time as the start time.
   *
   * @param importId the unique identifier for the import process
   * @param clock the source of the current time used to record the start time
   * @return a new instance of {@code ImportStatusEntity}
   */
  public static ImportStatusEntity start(String importId, InstantSource clock) {
    return new ImportStatusEntity(importId, clock.instant());
  }

  /**
   * Marks the current import process as completed and records the completion time.
   *
   * @param clock the source of the current time used to set the completion timestamp
   */
  public void markCompleted(InstantSource clock) {
    this.status = ImportStatus.COMPLETED;
    this.completedAt = clock.instant();
  }

  /**
   * Marks the current import process as failed and records the failure time.
   *
   * @param clock the source of the current time used to set the failure timestamp
   */
  public void markFailed(InstantSource clock) {
    this.status = ImportStatus.FAILED;
    this.completedAt = clock.instant();
  }

  /**
   * Represents the various states of an import process within the system.
   *
   * <ul>
   *   <li>PROCESSING - The import process is currently in progress.
   *   <li>COMPLETED - The import process has finished successfully.
   *   <li>FAILED - The import process has failed to complete.
   * </ul>
   */
  public enum ImportStatus {
    PROCESSING,
    COMPLETED,
    FAILED
  }
}
