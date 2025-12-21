package com.io.tedtalks.controller;

import com.io.tedtalks.dto.ImportInitResponse;
import com.io.tedtalks.dto.ImportStatusResponse;
import com.io.tedtalks.service.CsvImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Controller providing endpoints for importing and tracking the status of CSV file imports. */
@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
@Tag(name = "CSV Import", description = "CSV file import endpoints")
public class CsvImportController {

  private final CsvImportService csvImportService;

  /**
   * Initiates the import process for a TED Talks CSV file.
   *
   * @param file the CSV file to be imported
   */
  @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.ACCEPTED)
  @Operation(summary = "Import TED Talks from CSV file")
  public ImportInitResponse importCsv(
      @Parameter(description = "CSV file to import") @RequestParam("file") MultipartFile file) {
    String importId = csvImportService.startImport(file);
    String statusUrl =
        ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/import/status/{importId}")
            .buildAndExpand(importId)
            .toUriString();
    return ImportInitResponse.of(importId, statusUrl);
  }

  /**
   * Retrieves the status of a CSV file import process by its unique import ID.
   *
   * @param importId the unique identifier of the import process
   */
  @GetMapping("/status/{importId}")
  @Operation(summary = "Get import status by ID")
  public ImportStatusResponse getImportStatus(@PathVariable String importId) {
    return csvImportService.getImportStatus(importId);
  }
}
