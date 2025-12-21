package com.io.tedtalks.service;

import com.io.tedtalks.dto.SpeakerInfluenceResponse;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.dto.YearlyInfluenceResponse;
import java.util.List;
import java.util.Optional;

/** Service interface for analyzing influence metrics of TED speakers and talks. */
public interface InfluenceAnalysisService {

  /**
   * Retrieves a list of the most influential TED speakers based on their aggregated influence
   * metrics.
   *
   * @param topN the maximum number of top influential speakers to retrieve
   * @return a list of {@code SpeakerInfluenceResponse} representing the most influential speakers
   */
  List<SpeakerInfluenceResponse> getMostInfluentialSpeakers(int topN);

  /**
   * Retrieves a list of the most influential TED Talks based on calculated influence metrics.
   *
   * @param topN the maximum number of top influential TED Talks to retrieve
   * @return a list of {@code TedTalkResponse} objects representing the most influential TED Talks
   */
  List<TedTalkResponse> getMostInfluentialTalks(int topN);

  /**
   * Retrieves a list of the most influential TED Talks by year based on influence metrics. Each
   * response item contains the year and the TED Talk with the highest influence score for that
   * year.
   *
   * @return a list of {@code YearlyInfluenceResponse} objects, where each represents the most
   *     influential TED Talk for a specific year
   */
  List<YearlyInfluenceResponse> getMostInfluentialTalkByYear();

  /**
   * Retrieves the aggregated influence metrics of a specific TED speaker.
   *
   * @param author the name of the speaker whose influence metrics are to be retrieved
   * @return an {@code Optional} containing the {@code SpeakerInfluenceResponse} with the detailed
   *     influence metrics of the speaker, or an empty {@code Optional} if the speaker is not found
   *     or has no associated TED Talks
   */
  Optional<SpeakerInfluenceResponse> getSpeakerInfluence(String author);
}
