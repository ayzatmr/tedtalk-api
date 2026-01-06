package com.io.tedtalks.repository;

import static com.io.tedtalks.jooq.tables.ImportStatus.IMPORT_STATUS;

import com.io.tedtalks.jooq.tables.pojos.ImportStatus;
import com.io.tedtalks.model.ImportStatusModel;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing ImportStatus related entities and interactions with the database.
 * It provides methods for saving, retrieving, and deleting ImportStatus instances.
 */
@Repository
@RequiredArgsConstructor
public class ImportStatusRepository {

  private final DSLContext dsl;

  /**
   * Saves the given ImportStatusModel into the database. If the record doesn't exist,
   * a new record is inserted. Otherwise, the existing record is updated.
   *
   * @param importStatus the ImportStatusModel to save, containing the details for the operation
   * @return the saved ImportStatusModel with updated data;
   *         returns null if the insert operation fails
   */
  public ImportStatusModel save(ImportStatusModel importStatus) {
    boolean exists = dsl.fetchExists(
        dsl.selectFrom(IMPORT_STATUS)
            .where(IMPORT_STATUS.IMPORT_ID.eq(importStatus.getImportId())));

    if (!exists) {
      var record =
          dsl.insertInto(IMPORT_STATUS)
              .set(IMPORT_STATUS.IMPORT_ID, importStatus.getImportId())
              .set(IMPORT_STATUS.STATUS, importStatus.getStatus())
              .set(IMPORT_STATUS.STARTED_AT, importStatus.getStartedAt())
              .set(IMPORT_STATUS.COMPLETED_AT, importStatus.getCompletedAt())
              .set(IMPORT_STATUS.VERSION, 0L)
              .returning()
              .fetchOne();

      return record != null ? new ImportStatusModel(record.into(ImportStatus.class)) : null;
    } else {
      dsl.update(IMPORT_STATUS)
          .set(IMPORT_STATUS.STATUS, importStatus.getStatus())
          .set(IMPORT_STATUS.COMPLETED_AT, importStatus.getCompletedAt())
          .where(IMPORT_STATUS.IMPORT_ID.eq(importStatus.getImportId()))
          .execute();

      return importStatus;
    }
  }

  public Optional<ImportStatusModel> findById(String importId) {
    return dsl
        .selectFrom(IMPORT_STATUS)
        .where(IMPORT_STATUS.IMPORT_ID.eq(importId))
        .fetchOptional()
        .map(r -> new ImportStatusModel(r.into(ImportStatus.class)));
  }

  public List<ImportStatusModel> findAll() {
    return dsl
        .selectFrom(IMPORT_STATUS)
        .fetch()
        .map(r -> new ImportStatusModel(r.into(ImportStatus.class)));
  }

  public void deleteById(String importId) {
    dsl.deleteFrom(IMPORT_STATUS).where(IMPORT_STATUS.IMPORT_ID.eq(importId)).execute();
  }
}
