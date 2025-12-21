package com.io.tedtalks.dto;

import java.util.List;

/**
 * Represents a paged response that contains a list of items and associated metadata about the
 * pagination state.
 *
 * @param <T> The type of elements in the paged response.
 */
public record PagedResponse<T>(List<T> rows, PageMetadata metadata) {

  public record PageMetadata(
      int page,
      int size,
      long totalElements,
      int totalPages,
      boolean first,
      boolean last,
      boolean hasNext,
      boolean hasPrevious) {}
}
