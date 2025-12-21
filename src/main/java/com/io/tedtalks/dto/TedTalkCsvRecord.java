package com.io.tedtalks.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * Represents a single record of TED Talk data, typically parsed from a CSV file.
 *
 * <p>The class is annotated with CSV binding annotations to map specific columns from a CSV file to
 * the respective fields in the object. Some fields are required based on the `@CsvBindByName`
 * annotations.
 */
@Data
public class TedTalkCsvRecord {

  @CsvBindByName(column = "title", required = true)
  private String title;

  @CsvBindByName(column = "author", required = true)
  private String author;

  @CsvBindByName(column = "date", required = true)
  private String date;

  @CsvBindByName(column = "views")
  private String views;

  @CsvBindByName(column = "likes")
  private String likes;

  @CsvBindByName(column = "link", required = true)
  private String link;
}
