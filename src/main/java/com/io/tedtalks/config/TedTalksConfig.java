package com.io.tedtalks.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Configuration class for the TED Talks application. */
@Validated
@ConfigurationProperties(prefix = "tedtalks")
public record TedTalksConfig(@Valid Csv csv, @Valid Influence influence) {

  /**
   * Represents the configuration properties for CSV processing in the TED Talks application.
   *
   * @param batchSize The batch size for processing CSV records.
   */
  public record Csv(@Min(1) int batchSize) {}

  /**
   * Represents the influence configuration properties for the TED Talks application.
   *
   * @param viewsWeight The weight attributed to the number of views (0.0 to 1.0 inclusive).
   * @param likesWeight The weight attributed to the number of likes (0.0 to 1.0 inclusive).
   */
  public record Influence(
      @DecimalMin("0.0") @DecimalMax("1.0") double viewsWeight,
      @DecimalMin("0.0") @DecimalMax("1.0") double likesWeight) {}
}
