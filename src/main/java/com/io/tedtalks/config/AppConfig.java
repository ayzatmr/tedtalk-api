package com.io.tedtalks.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.time.InstantSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  /**
   * Virtual-thread executor with bounded queue. Limits concurrent imports to prevent
   * database contention. Automatically shut down by Spring on context close.
   */
  @Bean(name = "csvImportExecutor", destroyMethod = "shutdown")
  public ExecutorService csvImportExecutor() {
    return Executors.newThreadPerTaskExecutor(
        Thread.ofVirtual().name("csv-import-", 0).factory());
  }

  @Bean
  public InstantSource instantSource() {
    return InstantSource.system();
  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("TED Talks API")
                .version("1.0.0")
                .description("API for managing and analyzing TED Talks"));
  }
}
