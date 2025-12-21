package com.io.tedtalks.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.io.tedtalks.entity.TedTalkEntity;
import java.time.YearMonth;

/** Represents a response containing the details of a TED Talk. */
public record TedTalkResponse(
    Long id,
    String title,
    String author,
    @JsonFormat(pattern = "yyyy-MM") YearMonth date,
    long views,
    long likes,
    String link,
    double influenceScore) {


  /**
   * Converts a {@link TedTalkEntity} instance into a {@link TedTalkResponse} instance and includes
   * the calculated influence score.
   *
   * @param entity the {@code TedTalkEntity} to be converted. Must not be null.
   * @param viewsWeight the weight assigned to the view count for calculating the influence score.
   * @param likesWeight the weight assigned to the like count for calculating the influence score.
   * @return a {@code TedTalkResponse} instance containing the data from the given entity and the
   *     specified influence score.
   */
  public static TedTalkResponse fromEntity(
      TedTalkEntity entity, double viewsWeight, double likesWeight) {
    return new TedTalkResponse(
        entity.getId(),
        entity.getTitle(),
        entity.getAuthor(),
        entity.getYearMonth(),
        entity.getViews(),
        entity.getLikes(),
        entity.getLink(),
        entity.calculateInfluence(viewsWeight, likesWeight));
  }
}
