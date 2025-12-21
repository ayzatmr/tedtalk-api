package com.io.tedtalks.repository;

import com.io.tedtalks.entity.TedTalkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for querying and persisting {@code TedTalkEntity} objects. This interface
 * extends the {@code JpaRepository} to provide basic CRUD operations and additional query methods
 * for filtering TED Talks based on specific criteria.
 */
@Repository
public interface TedTalkRepository extends JpaRepository<TedTalkEntity, Long> {

  /**
   * Retrieves a paginated list of TED Talk entities.
   *
   * @param author the name of the author to filter by; supports partial matches with
   *     case-insensitive comparison. If null or empty, the filter is not applied.
   * @param year the year of the TED Talk to filter by. If null, the filter is not applied.
   * @param keyword a keyword to filter by, which matches parts of the title or author
   *     case-insensitively. If null or empty, the filter is not applied.
   * @param pageable the pagination and sorting information.
   * @return a paginated list of {@code TedTalkEntity} objects that match the applied filters.
   */
  @Query(
      value =
          """
                  SELECT * FROM ted_talks t
                  WHERE (:author IS NULL OR LOWER(t.author) LIKE LOWER(CONCAT(:author, '%')))
                    AND (:year IS NULL OR t.year_value = :year)
                    AND (:keyword IS NULL
                         OR LOWER(t.title) LIKE LOWER(CONCAT(:keyword, '%'))
                         OR LOWER(t.author) LIKE LOWER(CONCAT(:keyword, '%')))
                  """,
      countQuery =
          """
                    SELECT COUNT(*) FROM ted_talks t
                    WHERE (:author IS NULL OR LOWER(t.author) LIKE LOWER(CONCAT(:author, '%')))
                      AND (:year IS NULL OR t.year_value = :year)
                      AND (:keyword IS NULL
                           OR LOWER(t.title) LIKE LOWER(CONCAT(:keyword, '%'))
                           OR LOWER(t.author) LIKE LOWER(CONCAT(:keyword, '%')))
                    """,
      nativeQuery = true)
  Page<TedTalkEntity> findByFilters(
      @Param("author") String author,
      @Param("year") Integer year,
      @Param("keyword") String keyword,
      Pageable pageable);
}
