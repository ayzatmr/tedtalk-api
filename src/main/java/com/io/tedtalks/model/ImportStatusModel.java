package com.io.tedtalks.model;

import com.io.tedtalks.jooq.tables.pojos.ImportStatus;
import java.time.InstantSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ImportStatusModel extends ImportStatus {

  public ImportStatusModel() {
    super();
  }

  public ImportStatusModel(ImportStatus value) {
    super(value);
  }

  public static ImportStatusModel start(String importId, InstantSource clock) {
    ImportStatusModel model = new ImportStatusModel();
    model.setImportId(importId);
    model.setStatus(ImportStatusEnum.PROCESSING.name());
    model.setStartedAt(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
    return model;
  }

  public void markCompleted(InstantSource clock) {
    setStatus(ImportStatusEnum.COMPLETED.name());
    setCompletedAt(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
  }

  public void markFailed(InstantSource clock) {
    setStatus(ImportStatusEnum.FAILED.name());
    setCompletedAt(LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC));
  }

  public enum ImportStatusEnum {
    PROCESSING,
    COMPLETED,
    FAILED
  }
}
