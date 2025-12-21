package com.io.tedtalks.exception;

/**
 * ResourceNotFoundException is a custom runtime exception that signals the absence of a requested
 * resource. It is typically used in scenarios where a resource referenced by the client cannot be
 * found, potentially leading to a 404 HTTP response in web applications.
 */
public final class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
