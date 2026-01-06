package com.io.tedtalks.dto;

import java.util.List;

/**
 * Represents the aggregated influence response of a speaker.
 *
 * <p>The response is designed to provide a consolidated view of a speaker's influence based on
 * their TED Talks.
 */
public record SpeakerInfluenceResponse(
    String author,
    double totalInfluence,
    long totalViews,
    long totalLikes,
    long talkCount,
    List<TedTalkResponse> talks) {

  /**
   * Creates an instance of {@code SpeakerInfluenceResponse} from a given {@code
   * SpeakerInfluenceDto}.
   *
   * @param dto The {@code SpeakerInfluenceDto} containing the aggregated influence metrics of a
   *     speaker.
   * @return A new {@code SpeakerInfluenceResponse} instance populated with the data from the
   *     provided DTO.
   */
  public static SpeakerInfluenceResponse fromDto(SpeakerInfluenceDto dto) {
    return new SpeakerInfluenceResponse(
        dto.author(),
        dto.totalInfluence(),
        dto.totalViews(),
        dto.totalLikes(),
        dto.talkCount(),
        List.of());
  }
}
