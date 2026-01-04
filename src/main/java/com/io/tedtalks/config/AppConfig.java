package com.io.tedtalks.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.time.InstantSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class defining application-level beans for the TED Talks project. */
@Configuration
@Slf4j
public class AppConfig {

  /**
   * Virtual-thread executor with bounded queue. Limits concurrent imports to prevent database
   * contention. Automatically shut down by Spring on context close.
   */
  @Bean(name = "csvImportExecutor", destroyMethod = "shutdown")
  public ExecutorService csvImportExecutor(TedTalksConfig config) {

    ThreadFactory virtualThreadFactory = Thread.ofVirtual().name("csv-import-", 0).factory();

    RejectedExecutionHandler rejectionPolicy =
        (runnable, executor) -> {
          log.warn(
              "CSV import rejected - queue is full. Active: {}, Queue: {}",
              executor.getActiveCount(),
              executor.getQueue().size());
          throw new RejectedExecutionException(
              "Too many concurrent imports. Please try again later.");
        };

    return new ThreadPoolExecutor(
        0,
        config.csv().maxConcurrentImports(),
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(config.csv().importQueueCapacity()),
        virtualThreadFactory,
        rejectionPolicy);
  }

  /**
   * Provides a bean for obtaining the current instant with system precision.
   *
   * @return an {@code InstantSource} configured to use the system clock.
   */
  @Bean
  public InstantSource instantSource() {
    return InstantSource.system();
  }

  /**
   * Provides an OpenAPI configuration bean for the TED Talk API.
   *
   * @return an {@code OpenAPI} instance containing metadata.
   */
  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("TED Talk API")
                .version("1.0.0")
                .description("API for managing and analyzing TED Talks"));
  }
}
