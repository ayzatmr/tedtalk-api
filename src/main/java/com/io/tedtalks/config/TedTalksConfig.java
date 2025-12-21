package com.io.tedtalks.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration class for TED Talks application-specific properties. The properties are prefixed
 * with "tedtalks" in the application configuration.
 */
@Component
@ConfigurationProperties(prefix = "tedtalks")
@Validated
@Getter
@Setter
public class TedTalksConfig {

  private Csv csv = new Csv();
  private Influence influence = new Influence();

  @Getter
  @Setter
  public static class Csv {
    @Min(1)
    private int batchSize = 1000;
  }

  @Getter
  @Setter
  public static class Influence {
    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double viewsWeight = 0.7;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double likesWeight = 0.3;
  }
}
