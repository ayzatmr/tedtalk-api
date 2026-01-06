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

  public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
    this(
        content,
        new PageMetadata(
            page,
            size,
            totalElements,
            totalPages,
            page == 0,
            page >= totalPages - 1,
            page < totalPages - 1,
            page > 0));
  }

  public PagedResponse(Page<T> page) {
    this(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  public static <T> PagedResponse<T> of(List<T> content, Page<?> page) {
    return new PagedResponse<>(List.copyOf(content), PageMetadata.from(page));
  }

  public record PageMetadata(
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
