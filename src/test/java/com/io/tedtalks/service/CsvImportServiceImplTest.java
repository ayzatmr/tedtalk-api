package com.io.tedtalks.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.io.tedtalks.config.TedTalksConfig;
import com.io.tedtalks.dto.ImportStatusResponse;
import com.io.tedtalks.entity.ImportStatusEntity;
import com.io.tedtalks.exception.CsvImportException;
import com.io.tedtalks.exception.ResourceNotFoundException;
import com.io.tedtalks.repository.ImportStatusRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.InstantSource;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
final class CsvImportServiceImplTest {

  @Mock private TedTalksConfig config;
  @Mock private TedTalkService tedTalkService;
  @Mock private ImportStatusRepository importStatusRepository;
  @Mock private ExecutorService csvImportExecutor;
  @Mock private InstantSource clock;
  @Mock private MultipartFile file;

  private CsvImportServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new CsvImportServiceImpl(
            config, tedTalkService, importStatusRepository, csvImportExecutor, clock);
  }

  @Test
  void startImport_shouldThrowExceptionWhenFileIsEmpty() {
    when(file.isEmpty()).thenReturn(true);

    assertThrows(CsvImportException.class, () -> service.startImport(file));

    verifyNoInteractions(importStatusRepository, csvImportExecutor);
  }

  @Test
  void startImport_shouldReturnImportId() throws IOException {
    Instant now = Instant.parse("2024-01-01T10:00:00Z");

    when(file.isEmpty()).thenReturn(false);
    when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
    when(clock.instant()).thenReturn(now);
    when(importStatusRepository.save(any(ImportStatusEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    doNothing().when(csvImportExecutor).execute(any(Runnable.class));

    String importId = service.startImport(file);

    assertNotNull(importId);
    verify(importStatusRepository).save(any(ImportStatusEntity.class));
    verify(csvImportExecutor).execute(any(Runnable.class));
  }

  @Test
  void getImportStatus_shouldReturnStatus() {
    Instant now = Instant.parse("2024-01-01T10:00:00Z");
    String importId = "test-id";

    when(clock.instant()).thenReturn(now);

    ImportStatusEntity entity = ImportStatusEntity.start(importId, clock);
    when(importStatusRepository.findById(importId)).thenReturn(Optional.of(entity));

    ImportStatusResponse response = service.getImportStatus(importId);

    assertNotNull(response);
    assertEquals(importId, response.importId());
    verify(importStatusRepository).findById(importId);
  }

  @Test
  void getImportStatus_shouldThrowExceptionWhenNotFound() {
    when(importStatusRepository.findById("missing")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getImportStatus("missing"));
  }
}
