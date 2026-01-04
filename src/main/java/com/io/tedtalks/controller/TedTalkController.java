package com.io.tedtalks.controller;

import com.io.tedtalks.dto.PagedResponse;
import com.io.tedtalks.dto.PaginationParams;
import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.service.TedTalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Controller class responsible for managing TED Talk resources. */
@RestController
@RequestMapping("/api/v1/talks")
@RequiredArgsConstructor
@Validated
@Tag(name = "TED Talks", description = "TED Talks management endpoints")
public class TedTalkController {

  private final TedTalkService tedTalkService;

  /**
   * Creates a new TED Talk resource.
   *
   * @param request the request containing details of the TED Talk; must be valid.
   * @return the response containing details of the created TED Talk.
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "Create a new TED Talk")
  public TedTalkResponse createTalk(@Valid @RequestBody TedTalkRequest request) {
    return tedTalkService.createTalk(request);
  }

  /**
   * Retrieves a paginated list of TED Talks with optional filtering and sorting criteria.
   *
   * @param author an optional parameter to filter TED Talks by author name.
   * @param year an optional parameter to filter TED Talks by the year of publication.
   * @param keyword an optional parameter to search for a keyword in the title or author name.
   * @param page the page number for pagination; defaults to 0 if not specified.
   * @param size the number of items per page; defaults to 100 if not specified, with a maximum
   *     value of 100.
   * @param sortBy the field by which to sort the results (e.g., title, author, views, likes, year).
   * @param sortDirection the direction of sorting; can be either ASC (ascending) or DESC
   *     (descending).
   * @return a paginated response containing a list of filtered and sorted TED Talks.
   */
  @GetMapping
  @Operation(summary = "Get TED Talks with optional filters (combined with AND)")
  public PagedResponse<TedTalkResponse> getTalks(
      @Parameter(description = "Filter by author name")
          @RequestParam(required = false)
          @Size(max = 255)
          String author,
      @Parameter(description = "Filter by year")
          @RequestParam(required = false)
          @Min(1500)
          @Max(2100)
          Integer year,
      @Parameter(description = "Search keyword in title and author")
          @RequestParam(required = false)
          @Size(max = 255)
          String keyword,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") @Min(0) int page,
      @Parameter(description = "Page size")
          @RequestParam(defaultValue = "100")
          @Min(1)
          @Max(100)
          int size,
      @Parameter(description = "Sort field (e.g., title, author, views, likes, year)")
          @RequestParam(defaultValue = "id")
          @Pattern(regexp = "id|title|author|views|likes|year", message = "Invalid sort field")
          String sortBy,
      @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC")
          Sort.Direction sortDirection) {

    PaginationParams params = PaginationParams.of(page, size, sortBy, sortDirection);
    return tedTalkService.getTalks(author, year, keyword, params.toPageable());
  }

  /**
   * Retrieves a TED Talk by its unique identifier.
   *
   * @param id the unique identifier of the TED Talk to retrieve; must not be null.
   * @return the response containing details of the requested TED Talk.
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get TED Talk by ID")
  public TedTalkResponse getTalkById(@PathVariable @Min(1) Long id) {
    return tedTalkService.getTalkById(id);
  }

  /**
   * Updates an existing TED Talk resource.
   *
   * @param id the unique identifier of the TED Talk to update; must not be null.
   * @param request the request containing updated details of the TED Talk; must be valid.
   * @return the response containing details of the updated TED Talk.
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update a TED Talk")
  public TedTalkResponse updateTalk(
      @PathVariable @Min(1) Long id, @Valid @RequestBody TedTalkRequest request) {
    return tedTalkService.updateTalk(id, request);
  }

  /**
   * Deletes a TED Talk identified by its unique ID.
   *
   * @param id the unique identifier of the TED Talk to delete; must not be null.
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a TED Talk")
  public void deleteTalk(@PathVariable @Min(1) Long id) {
    tedTalkService.deleteTalk(id);
  }
}
