package com.io.tedtalks.jobs;

import com.io.tedtalks.model.ImportStatusModel;
import com.io.tedtalks.repository.ImportStatusRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.InstantSource;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportStartupListener {

  private final ImportStatusRepository importStatusRepository;

  /**
   * Cleanup stuck imports after application restart. Marks PROCESSING imports as FAILED if they
   * were started long ago.
   */
  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void cleanupDbStuckImports() {

    Instant threshold = Instant.now().minus(Duration.ofMinutes(30));

    List<ImportStatusModel> stuckImports =
        importStatusRepository.findAll().stream()
            .filter(
                i ->
                    i.getStatus()
                        .equals(ImportStatusModel.ImportStatusEnum.PROCESSING.name()))
            .filter(
                i ->
                    i.getStartedAt()
                        .toInstant(ZoneOffset.UTC)
                        .isBefore(threshold))
            .toList();

    if (stuckImports.isEmpty()) {
      log.info("No stuck imports found on startup");
      return;
    }

    stuckImports.forEach(i -> i.markFailed(InstantSource.system()));
    stuckImports.forEach(importStatusRepository::save);

    log.warn("Marked {} stuck imports as FAILED on startup", stuckImports.size());
  }

  /**
   * Deletes temporary files matching a specific naming pattern from the system's temporary
   * directory on application startup.
   *
   * <p>This method is automatically triggered when the application is fully started, using the
   * {@link ApplicationReadyEvent}. It scans the temporary directory for files with names starting
   * with "csv-import-", attempts to delete them, and logs the outcome of each deletion. If an error
   * occurs during the processing of a file or during directory scanning, the error is logged.
   *
   * <p>Logging allows visibility into successful or unsuccessful cleanup operations, ensuring that
   * cleanup behavior can be monitored effectively.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void cleanupTempFiles() {

    Path tempDir = Path.of(System.getProperty("java.io.tmpdir"));

    try (var files = Files.list(tempDir)) {
      files
          .filter(p -> p.getFileName().toString().startsWith("csv-import-"))
          .forEach(
              p -> {
                try {
                  Files.deleteIfExists(p);
                  log.warn("Deleted stale temp file on startup: {}", p);
                } catch (Exception e) {
                  log.error("Failed to delete temp file {}", p, e);
                }
              });
    } catch (Exception e) {
      log.error("Failed to cleanup temp files on startup", e);
    }
  }
}
