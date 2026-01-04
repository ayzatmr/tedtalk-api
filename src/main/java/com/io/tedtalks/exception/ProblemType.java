package com.io.tedtalks.exception;

import java.net.URI;

/**
 * Enumeration of standardized problem types for the TED Talks API. Each type represents a specific
 * category of error with a unique URN identifier.
 */
public enum ProblemType {
  RESOURCE_NOT_FOUND("resource-not-found"),
  CSV_IMPORT_ERROR("csv-import-error"),
  TOO_MANY_IMPORTS("too-many-imports"),
  VALIDATION_ERROR("validation-error"),
  CONSTRAINT_VIOLATION("constraint-violation"),
  INTERNAL_ERROR("internal-error");

  private static final String URN_PREFIX = "urn:ted-talks:";
  private final String type;

  ProblemType(String type) {
    this.type = type;
  }

  public URI toUri() {
    return URI.create(URN_PREFIX + type);
  }
}
