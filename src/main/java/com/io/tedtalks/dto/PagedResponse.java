package com.io.tedtalks.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Represents a paged response that contains a list of items and associated metadata about the
 * pagination state.
 *
 * @param <T> The type of elements in the paged response.
 */
public record PagedResponse<T>(List<T> rows, PageMetadata metadata) {

  /**
   * Creates a new instance of {@code PagedResponse} containing the provided list of elements along
   * with the metadata extracted from the given page information.
   *
   * @param <T> The type of elements in the paged response.
   * @param content The list of elements to include in the response.
   * @param page The page information containing metadata about the pagination state.
   * @return A new {@code PagedResponse} instance with the specified content and metadata.
   */
  public static <T> PagedResponse<T> of(List<T> content, Page<?> page) {
    return new PagedResponse<>(List.copyOf(content), PageMetadata.from(page));
  }

  record PageMetadata(
      int page,
      int size,
      long totalElements,
      int totalPages,
      boolean first,
      boolean last,
      boolean hasNext,
      boolean hasPrevious) {

    /**
     * Creates a new instance of {@code PageMetadata} based on the given {@code Page} object.
     *
     * @param page the {@code Page} object containing details about the current page.
     * @return a {@code PageMetadata} instance representing the metadata of the given page.
     */
    static PageMetadata from(Page<?> page) {
      return new PageMetadata(
          page.getNumber(),
          page.getSize(),
          page.getTotalElements(),
          page.getTotalPages(),
          page.isFirst(),
          page.isLast(),
          page.hasNext(),
          page.hasPrevious());
    }
  }
}
