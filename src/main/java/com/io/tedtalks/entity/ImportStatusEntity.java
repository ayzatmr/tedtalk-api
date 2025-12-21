package com.io.tedtalks.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
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
