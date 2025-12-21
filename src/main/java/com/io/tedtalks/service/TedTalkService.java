package com.io.tedtalks.service;

import com.io.tedtalks.dto.PagedResponse;
import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.dto.TedTalkResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

/** Service interface defining the operations related to TED Talks management. */
public interface TedTalkService {

  /**
   * Creates a new TED Talk entry based on the provided request data.
   *
   * @param request an instance of {@code TedTalkRequest} containing the details of the TED Talk to
   *     be created.
   * @return a {@code TedTalkResponse} representing the newly created TED Talk entry
   */
  TedTalkResponse createTalk(TedTalkRequest request);

  /**
   * Updates an existing TED Talk entry based on the provided identifier and request data.
   *
   * @param id the unique identifier of the TED Talk to be updated
   * @param request an instance of {@code TedTalkRequest} containing the updated details of the TED
   *     Talk
   * @return a {@code TedTalkResponse} representing the updated TED Talk entry
   */
  TedTalkResponse updateTalk(Long id, TedTalkRequest request);

  /**
   * Deletes a TED Talk entry identified by the given unique identifier.
   *
   * @param id the unique identifier of the TED Talk to be deleted
   */
  void deleteTalk(Long id);

  /**
   * Retrieves a TED Talk response by its unique identifier.
   *
   * @param id the unique identifier of the TED Talk to retrieve
   * @return a {@code TedTalkResponse} containing the details of the TED Talk with the specified
   *     identifier
   */
  TedTalkResponse getTalkById(Long id);

  /**
   * Retrieves a paginated list of TED Talks based on the provided filter criteria.
   *
   * @param author the name of the author to filter talks by; if null, no filtering by author is
   *     applied
   * @param year the year to filter talks by; if null, no filtering by year is applied
   * @param keyword a keyword to filter talks by matching titles or descriptions; if null, no
   *     filtering by keyword is applied
   * @param pageable a {@code Pageable} object specifying pagination information; cannot be null
   * @return a {@code PagedResponse<TedTalkResponse>} containing the list of TED Talks matching the
   *     filter criteria along with pagination metadata
   */
  PagedResponse<TedTalkResponse> getTalks(
      String author, Integer year, String keyword, Pageable pageable);

  /**
   * Creates multiple TED Talk entries in a batch operation for efficient bulk imports.
   *
   * @param requests a list of {@code TedTalkRequest} objects containing the details of the TED
   *     Talks to be created
   */
  void createTalksBatch(List<TedTalkRequest> requests);
}
