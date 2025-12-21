package com.io.tedtalks.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.io.tedtalks.config.TedTalksConfig;
import com.io.tedtalks.dto.InfluentialTalkDto;
import com.io.tedtalks.dto.SpeakerInfluenceDto;
import com.io.tedtalks.dto.SpeakerInfluenceResponse;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.dto.YearlyInfluenceResponse;
import com.io.tedtalks.repository.InfluenceAnalysisRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
final class InfluenceAnalysisServiceImplTest {

  private static final double VIEWS_WEIGHT = 0.7;
  private static final double LIKES_WEIGHT = 0.3;

  @Mock private InfluenceAnalysisRepository analyticsRepository;
  @Mock private TedTalksConfig config;
  @Mock private TedTalksConfig.Influence influenceConfig;

  private InfluenceAnalysisServiceImpl service;

  @BeforeEach
  void setUp() {
    when(config.getInfluence()).thenReturn(influenceConfig);
    when(influenceConfig.getViewsWeight()).thenReturn(VIEWS_WEIGHT);
    when(influenceConfig.getLikesWeight()).thenReturn(LIKES_WEIGHT);
    
    service = new InfluenceAnalysisServiceImpl(analyticsRepository, config);
  }

  @Test
  void getMostInfluentialSpeakers_shouldReturnList() {
    SpeakerInfluenceDto dto = new SpeakerInfluenceDtoStub("John Doe", 5000L, 500L, 1000.0, 3L);

    when(analyticsRepository.findMostInfluentialSpeakers(VIEWS_WEIGHT, LIKES_WEIGHT, 5))
        .thenReturn(List.of(dto));

    List<SpeakerInfluenceResponse> result = service.getMostInfluentialSpeakers(5);

    assertEquals(1, result.size());
    assertEquals("John Doe", result.getFirst().author());

    verify(analyticsRepository).findMostInfluentialSpeakers(VIEWS_WEIGHT, LIKES_WEIGHT, 5);
    verifyNoMoreInteractions(analyticsRepository);
  }

  @Test
  void getMostInfluentialTalks_shouldReturnList() {
    InfluentialTalkDto dto =
        new InfluentialTalkDtoStub(
            1L, "Test Talk", "John Doe", 2020, 1, 1000L, 100L, "http://test.com", 800.0);

    when(analyticsRepository.findMostInfluentialTalks(VIEWS_WEIGHT, LIKES_WEIGHT, 5))
        .thenReturn(List.of(dto));

    List<TedTalkResponse> result = service.getMostInfluentialTalks(5);

    assertEquals(1, result.size());
    assertEquals("Test Talk", result.getFirst().title());

    verify(analyticsRepository).findMostInfluentialTalks(VIEWS_WEIGHT, LIKES_WEIGHT, 5);
    verifyNoMoreInteractions(analyticsRepository);
  }

  @Test
  void getMostInfluentialTalkByYear_shouldReturnList() {
    InfluentialTalkDto dto =
        new InfluentialTalkDtoStub(
            1L, "Test Talk", "John Doe", 2020, 1, 1000L, 100L, "http://test.com", 800.0);

    when(analyticsRepository.findMostInfluentialTalkPerYear(VIEWS_WEIGHT, LIKES_WEIGHT))
        .thenReturn(List.of(dto));

    List<YearlyInfluenceResponse> result = service.getMostInfluentialTalkByYear();

    assertEquals(1, result.size());
    assertEquals(2020, result.getFirst().year());
    assertEquals("Test Talk", result.getFirst().mostInfluentialTalk().title());

    verify(analyticsRepository).findMostInfluentialTalkPerYear(VIEWS_WEIGHT, LIKES_WEIGHT);
    verifyNoMoreInteractions(analyticsRepository);
  }

  @Test
  void getSpeakerInfluence_shouldReturnEmpty() {
    when(analyticsRepository.findSpeakerInfluence("Unknown", VIEWS_WEIGHT, LIKES_WEIGHT))
        .thenReturn(Optional.empty());

    Optional<SpeakerInfluenceResponse> result = service.getSpeakerInfluence("Unknown");

    assertTrue(result.isEmpty());

    verify(analyticsRepository).findSpeakerInfluence("Unknown", VIEWS_WEIGHT, LIKES_WEIGHT);
    verifyNoMoreInteractions(analyticsRepository);
  }

  @Test
  void getSpeakerInfluence_shouldReturnSpeaker() {
    SpeakerInfluenceDto dto = new SpeakerInfluenceDtoStub("John Doe", 5000L, 500L, 1000.0, 3L);

    when(analyticsRepository.findSpeakerInfluence("John Doe", VIEWS_WEIGHT, LIKES_WEIGHT))
        .thenReturn(Optional.of(dto));

    Optional<SpeakerInfluenceResponse> result = service.getSpeakerInfluence("John Doe");

    assertTrue(result.isPresent());
    assertEquals("John Doe", result.get().author());

    verify(analyticsRepository).findSpeakerInfluence("John Doe", VIEWS_WEIGHT, LIKES_WEIGHT);
    verifyNoMoreInteractions(analyticsRepository);
  }

  private record InfluentialTalkDtoStub(
      Long id,
      String title,
      String author,
      Integer year,
      Integer month,
      Long views,
      Long likes,
      String link,
      Double influence)
      implements InfluentialTalkDto {

    @Override
    public Long getId() {
      return id;
    }

    @Override
    public String getTitle() {
      return title;
    }

    @Override
    public String getAuthor() {
      return author;
    }

    @Override
    public Integer getYearValue() {
      return year;
    }

    @Override
    public Integer getMonthValue() {
      return month;
    }

    @Override
    public Long getViews() {
      return views;
    }

    @Override
    public Long getLikes() {
      return likes;
    }

    @Override
    public String getLink() {
      return link;
    }

    @Override
    public Double getInfluence() {
      return influence;
    }
  }

  private record SpeakerInfluenceDtoStub(
      String author, Long totalViews, Long totalLikes, Double totalInfluence, Long talkCount)
      implements SpeakerInfluenceDto {

    @Override
    public String getAuthor() {
      return author;
    }

    @Override
    public Long getTotalViews() {
      return totalViews;
    }

    @Override
    public Long getTotalLikes() {
      return totalLikes;
    }

    @Override
    public Double getTotalInfluence() {
      return totalInfluence;
    }

    @Override
    public Long getTalkCount() {
      return talkCount;
    }
  }
}
