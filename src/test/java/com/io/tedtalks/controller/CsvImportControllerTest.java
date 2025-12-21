package com.io.tedtalks.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.io.tedtalks.dto.ImportStatusResponse;
import com.io.tedtalks.service.CsvImportService;
import java.time.Instant;
import java.time.InstantSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CsvImportController.class)
final class CsvImportControllerTest {

  private static final String BASE_URL = "/api/v1/import";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CsvImportService csvImportService;

  @MockitoBean private InstantSource instantSource;

  @Test
  void importCsv_shouldReturnAccepted() throws Exception {
    String importId = "test-import-id";

    when(csvImportService.startImport(any())).thenReturn(importId);

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "test.csv", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes());

    mockMvc
        .perform(multipart(BASE_URL + "/csv").file(file))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.importId").value(importId))
        .andExpect(jsonPath("$.statusUrl").isNotEmpty());
  }


  @Test
  void getImportStatus_shouldReturnStatus() throws Exception {
    String importId = "test-import-id";
    Instant now = Instant.parse("2024-01-01T10:00:00Z");

    ImportStatusResponse response =
        new ImportStatusResponse(importId, ImportStatusResponse.ImportStatus.COMPLETED, now, now);

    when(csvImportService.getImportStatus(importId)).thenReturn(response);

    mockMvc
        .perform(get(BASE_URL + "/status/{importId}", importId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.importId").value(importId))
        .andExpect(jsonPath("$.status").value("COMPLETED"));
  }
}
