package com.io.tedtalks.dto;

import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** Represents the parameters used for pagination and sorting in a pageable request. */
public record PaginationParams(int page, int size, String sortBy, Sort.Direction sortDirection) {

  private static final int MAX_SIZE = 100;
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("id", "title", "author", "year", "views", "likes");

  /**
   * Creates a new instance of {@code PaginationParams} with sanitized and validated pagination and
   * sorting parameters.
   *
   * @param page the requested page number. Values less than 0 will be treated as 0.
   * @param size the number of items per page. Values less than 1 will be set to 1, and values
   *     greater than the maximum size will be capped at the maximum size.
   * @param sortBy the field by which the results should be sorted. If null or invalid, a default
   *     value of "id" is used.
   * @param sortDirection the direction of sorting, either ascending or descending. If null, the
   *     default direction is ascending.
   * @return a {@code PaginationParams} instance containing the sanitized pagination and sorting
   *     parameters.
   */
  public static PaginationParams of(
      int page, int size, String sortBy, Sort.Direction sortDirection) {

    int safePage = Math.max(page, 0);
    int safeSize = Math.max(1, Math.min(size, MAX_SIZE));

    String safeSortBy = (sortBy != null && ALLOWED_SORT_FIELDS.contains(sortBy)) ? sortBy : "id";

    Sort.Direction safeDirection = sortDirection != null ? sortDirection : Sort.Direction.ASC;

    return new PaginationParams(safePage, safeSize, safeSortBy, safeDirection);
  }

  /**
   * Converts the current pagination and sorting parameters into a {@code Pageable} object.
   *
   * @return a {@code Pageable} instance representing the page number, size, and sorting criteria
   *     specified within this object.
   */
  public Pageable toPageable() {
    return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
  }
}
