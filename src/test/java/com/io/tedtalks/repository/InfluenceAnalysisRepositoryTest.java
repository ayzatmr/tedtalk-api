package com.io.tedtalks.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.io.tedtalks.dto.InfluentialTalkDto;
import com.io.tedtalks.dto.SpeakerInfluenceDto;
import com.io.tedtalks.entity.TedTalkEntity;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class InfluenceAnalysisRepositoryTest {

  private static final double VIEW_WEIGHT = 0.7;
  private static final double LIKE_WEIGHT = 0.3;

  @Autowired private InfluenceAnalysisRepository repository;

  @Autowired private TedTalkRepository tedTalkRepository;

  @Test
  void findMostInfluentialTalks_shouldReturnOrderedByInfluence() {
    tedTalkRepository.save(talk("Talk 1", "John Doe", 2020, 1, 1000, 100));
    tedTalkRepository.save(talk("Talk 2", "Jane Smith", 2020, 1, 5000, 500));

    List<InfluentialTalkDto> result =
        repository.findMostInfluentialTalks(VIEW_WEIGHT, LIKE_WEIGHT, 10);

    assertEquals(2, result.size());
    assertTrue(result.get(0).getInfluence() > result.get(1).getInfluence());
    assertEquals("Talk 2", result.get(0).getTitle());
  }

  @Test
  void findMostInfluentialSpeakers_shouldGroupByAuthor() {
    tedTalkRepository.save(talk("Talk 1", "John Doe", 2020, 1, 1000, 100));
    tedTalkRepository.save(talk("Talk 2", "John Doe", 2020, 2, 2000, 200));
    tedTalkRepository.save(talk("Talk 3", "Jane Smith", 2020, 1, 3000, 300));

    List<SpeakerInfluenceDto> result =
        repository.findMostInfluentialSpeakers(VIEW_WEIGHT, LIKE_WEIGHT, 10);

    assertEquals(2, result.size());

    SpeakerInfluenceDto john =
        result.stream().filter(s -> s.getAuthor().equals("John Doe")).findFirst().orElseThrow();

    assertEquals(3000L, john.getTotalViews());
    assertEquals(300L, john.getTotalLikes());
    assertEquals(2L, john.getTalkCount());
  }

  @Test
  void findSpeakerInfluence_shouldReturnSpeakerData() {
    tedTalkRepository.save(talk("Talk 1", "John Doe", 2020, 1, 1000, 100));
    tedTalkRepository.save(talk("Talk 2", "John Doe", 2020, 2, 2000, 200));

    Optional<SpeakerInfluenceDto> result =
        repository.findSpeakerInfluence("John Doe", VIEW_WEIGHT, LIKE_WEIGHT);

    assertTrue(result.isPresent());

    SpeakerInfluenceDto speaker = result.get();
    assertEquals("John Doe", speaker.getAuthor());
    assertEquals(3000L, speaker.getTotalViews());
    assertEquals(300L, speaker.getTotalLikes());
    assertEquals(2L, speaker.getTalkCount());
  }

  @Test
  void findSpeakerInfluence_shouldReturnEmptyForUnknownSpeaker() {
    Optional<SpeakerInfluenceDto> result =
        repository.findSpeakerInfluence("Unknown Speaker", VIEW_WEIGHT, LIKE_WEIGHT);

    assertTrue(result.isEmpty());
  }

  @Test
  void findSpeakerInfluence_shouldBeCaseInsensitive() {
    tedTalkRepository.save(talk("Talk 1", "John Doe", 2020, 1, 1000, 100));

    Optional<SpeakerInfluenceDto> result =
        repository.findSpeakerInfluence("john doe", VIEW_WEIGHT, LIKE_WEIGHT);

    assertTrue(result.isPresent());
    assertEquals("John Doe", result.get().getAuthor());
  }

  @Test
  void findMostInfluentialTalkPerYear_shouldReturnOnePerYear() {
    tedTalkRepository.save(talk("Talk 2020 A", "John Doe", 2020, 1, 1000, 100));
    tedTalkRepository.save(talk("Talk 2020 B", "Jane Smith", 2020, 2, 5000, 500));
    tedTalkRepository.save(talk("Talk 2021", "Bob Brown", 2021, 1, 3000, 300));

    List<InfluentialTalkDto> result =
        repository.findMostInfluentialTalkPerYear(VIEW_WEIGHT, LIKE_WEIGHT);

    assertEquals(2, result.size());

    assertEquals("Talk 2020 B", result.get(0).getTitle());
    assertEquals(2020, result.get(0).getYearValue());

    assertEquals("Talk 2021", result.get(1).getTitle());
    assertEquals(2021, result.get(1).getYearValue());
  }

  @Test
  void findMostInfluentialTalkPerYear_shouldReturnEmptyWhenNoTalks() {
    List<InfluentialTalkDto> result =
        repository.findMostInfluentialTalkPerYear(VIEW_WEIGHT, LIKE_WEIGHT);

    assertTrue(result.isEmpty());
  }

  private static TedTalkEntity talk(
      String title, String author, int year, int month, int views, int likes) {

    return TedTalkEntity.of(
        title,
        author,
        YearMonth.of(year, month),
        views,
        likes,
        "http://test.com/" + title.replace(" ", "-"));
  }
}
