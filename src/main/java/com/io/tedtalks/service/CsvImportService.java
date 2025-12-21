package com.io.tedtalks.service;

import com.io.tedtalks.dto.ImportStatusResponse;
import org.springframework.web.multipart.MultipartFile;

/** Service interface for importing data from CSV files into the system. */
public interface CsvImportService {

  /**
   * Initiates the process of importing data from the provided CSV file.
   *
   * @param file the MultipartFile representing the CSV file to be imported
   * @return a String representing the unique identifier for the initiated import process
   */
  String startImport(MultipartFile file);

  /**
   * Retrieves the current status of an import operation based on its unique identifier.
   *
   * @param importId the unique identifier of the import operation whose status is to be retrieved
   * @return an {@code ImportStatusResponse} containing details about the current status of the
   *     import operation
   */
  ImportStatusResponse getImportStatus(String importId);
}
