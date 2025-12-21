package com.io.tedtalks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The TedTalkApplication class serves as the entry point for the TED Talks application. It
 * initializes the application context and starts the Spring Boot application.
 */
@SpringBootApplication
public class TedTalkApplication {

  public static void main(String[] args) {
    SpringApplication.run(TedTalkApplication.class, args);
  }
}
