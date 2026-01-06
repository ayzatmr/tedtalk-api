package com.io.tedtalks.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.io.tedtalks.model.TedTalk;
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
   * Converts an {@link InfluentialTalkDto} instance into a {@link TedTalkResponse} instance.
   *
   * @param dto the {@code InfluentialTalkDto} to be converted. Must not be null.
   * @return a {@code TedTalkResponse} instance containing the data from the given DTO.
   */
  public static TedTalkResponse fromDto(InfluentialTalkDto dto) {
    return new TedTalkResponse(
        dto.id(),
        dto.title(),
        dto.author(),
        YearMonth.of(dto.yearValue(), dto.monthValue()),
        dto.views(),
        dto.likes(),
        dto.link(),
        dto.influence());
  }

  /**
   * Converts a {@link TedTalk} entity into a {@link TedTalkResponse} instance, using specified weights
   * to calculate the influence score.
   *
   * @param entity the {@code TedTalk} entity to be converted. Must not be null.
   * @param viewsWeight the weight to apply to the views count when calculating the influence score.
   * @param likesWeight the weight to apply to the likes count when calculating the influence score.
   * @return a {@code TedTalkResponse} instance containing the data from the given entity,
   *         including the calculated influence score.
   */
  public static TedTalkResponse fromEntity(
      TedTalk entity, double viewsWeight, double likesWeight) {
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
