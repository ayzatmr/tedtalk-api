package com.io.tedtalks.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "tedtalks")
public record TedTalksConfig(@Valid Csv csv, @Valid Influence influence) {

  public record Csv(@Min(1) int batchSize) {}

  public record Influence(
      @DecimalMin("0.0") @DecimalMax("1.0") double viewsWeight,
      @DecimalMin("0.0") @DecimalMax("1.0") double likesWeight) {}
}
