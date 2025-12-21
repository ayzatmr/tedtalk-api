package com.io.tedtalks.service;

import com.io.tedtalks.config.TedTalksConfig;
import com.io.tedtalks.dto.ImportStatusResponse;
import com.io.tedtalks.dto.TedTalkCsvRecord;
import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.entity.ImportStatusEntity;
import com.io.tedtalks.exception.CsvImportException;
import com.io.tedtalks.exception.ResourceNotFoundException;
import com.io.tedtalks.repository.ImportStatusRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.InstantSource;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service implementation for importing TED Talks data from CSV files.
 *
 * <p>This implementation is designed to process large CSV files asynchronously using an executor
 * for background processing.
 */
@Service
@Slf4j
public final class CsvImportServiceImpl implements CsvImportService {

  private static final DateTimeFormatter[] DATE_FORMATTERS = {
    DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH),
    DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)
  };

  private final TedTalksConfig config;
  private final TedTalkService tedTalkService;
  private final ImportStatusRepository importStatusRepository;
  private final ExecutorService csvImportExecutor;
  private final InstantSource clock;

  public CsvImportServiceImpl(
      TedTalksConfig config,
      TedTalkService tedTalkService,
      ImportStatusRepository importStatusRepository,
      ExecutorService csvImportExecutor,
      InstantSource clock) {

    this.config = config;
    this.tedTalkService = tedTalkService;
    this.importStatusRepository = importStatusRepository;
    this.csvImportExecutor = csvImportExecutor;
    this.clock = clock;
  }

  private static String safe(TedTalkCsvRecord r) {
    try {
      return r.getLink();
    } catch (Exception e) {
      return "<unknown>";
    }
  }

  @Override
  public String startImport(MultipartFile file) {
    if (file.isEmpty()) {
      throw new CsvImportException("File is empty");
    }

    String importId = UUID.randomUUID().toString();
    Path tempFile = createTempFile(file);

    importStatusRepository.save(ImportStatusEntity.start(importId, clock));

    csvImportExecutor.execute(
        () -> {
          try {
            processImport(importId, tempFile);
          } finally {
            deleteTempFile(tempFile);
          }
        });

    return importId;
  }

  @Override
  public ImportStatusResponse getImportStatus(String importId) {
    ImportStatusEntity entity =
        importStatusRepository
            .findById(importId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Import not found with id: " + importId));

    return ImportStatusResponse.fromEntity(entity);
  }

  public void processImport(String importId, Path csvFile) {

    int batchSize = config.csv().batchSize();
    List<TedTalkRequest> batch = new ArrayList<>(batchSize);

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(Files.newInputStream(csvFile)))) {

      CsvToBean<TedTalkCsvRecord> csv =
          new CsvToBeanBuilder<TedTalkCsvRecord>(reader)
              .withMappingStrategy(mappingStrategy())
              .withIgnoreEmptyLine(true)
              .withIgnoreLeadingWhiteSpace(true)
              .withThrowExceptions(false)
              .build();

      for (TedTalkCsvRecord record : csv) {
        try {
          batch.add(toRequest(record));
        } catch (Exception e) {
          log.warn("Invalid record skipped [{}]", safe(record));
          continue;
        }

        if (batch.size() == batchSize) {
          tedTalkService.createTalksBatch(batch);
          batch.clear();
        }
      }

      if (!batch.isEmpty()) {
        tedTalkService.createTalksBatch(batch);
      }

      markCompleted(importId);

    } catch (Exception e) {
      log.error("CSV import failed [{}]", importId, e);
      markFailed(importId);
      throw new CsvImportException("Import failed", e);
    }
  }

  private Path createTempFile(MultipartFile file) {
    try {
      Path tempFile = Files.createTempFile("csv-import-", ".csv");
      Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
      return tempFile;
    } catch (IOException e) {
      throw new CsvImportException("Failed to create temp file", e);
    }
  }

  private void deleteTempFile(Path path) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      log.warn("Failed to delete temp file: {}", path, e);
    }
  }

  private void markCompleted(String importId) {
    ImportStatusEntity status = importStatusRepository.findById(importId).orElseThrow();
    status.markCompleted(clock);
    importStatusRepository.save(status);
  }

  private void markFailed(String importId) {
    ImportStatusEntity status = importStatusRepository.findById(importId).orElseThrow();
    status.markFailed(clock);
    importStatusRepository.save(status);
  }

  private HeaderColumnNameMappingStrategy<TedTalkCsvRecord> mappingStrategy() {
    HeaderColumnNameMappingStrategy<TedTalkCsvRecord> strategy =
        new HeaderColumnNameMappingStrategy<>();
    strategy.setType(TedTalkCsvRecord.class);
    return strategy;
  }

  private TedTalkRequest toRequest(TedTalkCsvRecord r) {
    return new TedTalkRequest(
        r.getTitle(),
        r.getAuthor(),
        parseDate(r.getDate()),
        parseLong(r.getViews()),
        parseLong(r.getLikes()),
        r.getLink());
  }

  private YearMonth parseDate(String value) {
    if (value == null || value.isBlank()) {
      throw new CsvImportException("Date is required");
    }

    for (DateTimeFormatter formatter : DATE_FORMATTERS) {
      try {
        return YearMonth.parse(value.trim(), formatter);
      } catch (DateTimeParseException ignored) {
      }
    }
    throw new CsvImportException("Invalid date: " + value);
  }

  private long parseLong(String value) {
    try {
      return Math.max(0, Long.parseLong(value.trim()));
    } catch (Exception e) {
      return 0;
    }
  }
}
