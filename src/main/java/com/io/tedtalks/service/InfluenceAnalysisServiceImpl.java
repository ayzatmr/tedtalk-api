package com.io.tedtalks.service;

import com.io.tedtalks.config.TedTalksConfig;
import com.io.tedtalks.dto.SpeakerInfluenceResponse;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.dto.YearlyInfluenceResponse;
import com.io.tedtalks.repository.InfluenceAnalysisRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link InfluenceAnalysisService} interface for analyzing influence metrics
 * of TED speakers and talks using predefined weights for views and likes.
 *
 * <p>This service fetches influence data from the {@link InfluenceAnalysisRepository} and uses
 * configurations provided by {@link TedTalksConfig} to calculate influence scores.
 */
@Service
public final class InfluenceAnalysisServiceImpl implements InfluenceAnalysisService {

  private final InfluenceAnalysisRepository analyticsRepository;
  private final double viewsWeight;
  private final double likesWeight;

  public InfluenceAnalysisServiceImpl(
      InfluenceAnalysisRepository analyticsRepository, TedTalksConfig config) {
    this.analyticsRepository = analyticsRepository;
    this.viewsWeight = config.influence().viewsWeight();
    this.likesWeight = config.influence().likesWeight();
  }

  @Override
  public List<SpeakerInfluenceResponse> getMostInfluentialSpeakers(int topN) {
    if (topN <= 0) {
      return List.of();
    }

    return analyticsRepository.findMostInfluentialSpeakers(viewsWeight, likesWeight, topN).stream()
        .map(SpeakerInfluenceResponse::fromDto)
        .toList();
  }

  @Override
  public List<TedTalkResponse> getMostInfluentialTalks(int topN) {
    if (topN <= 0) {
      return List.of();
    }

    return analyticsRepository.findMostInfluentialTalks(viewsWeight, likesWeight, topN).stream()
        .map(TedTalkResponse::fromDto)
        .toList();
  }

  @Override
  public List<YearlyInfluenceResponse> getMostInfluentialTalkByYear() {
    return analyticsRepository.findMostInfluentialTalkPerYear(viewsWeight, likesWeight).stream()
        .map(dto -> new YearlyInfluenceResponse(dto.getYearValue(), TedTalkResponse.fromDto(dto)))
        .toList();
  }

  @Override
  public Optional<SpeakerInfluenceResponse> getSpeakerInfluence(String author) {
    return analyticsRepository
        .findSpeakerInfluence(author, viewsWeight, likesWeight)
        .map(SpeakerInfluenceResponse::fromDto);
  }
}
