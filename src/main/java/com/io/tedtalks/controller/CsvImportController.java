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
public final class CsvImportController {

  private final CsvImportService csvImportService;
}
