package com.io.tedtalks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The TedTalksApplication class serves as the entry point for the TED Talks application. It
 * initializes the application context and starts the Spring Boot application.
 */
@SpringBootApplication
public class TedTalksApplication {

  public static void main(String[] args) {
    SpringApplication.run(TedTalksApplication.class, args);
  }
}
