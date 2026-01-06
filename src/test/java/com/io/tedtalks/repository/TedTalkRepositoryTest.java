package com.io.tedtalks.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.io.tedtalks.dto.PagedResponse;
import com.io.tedtalks.model.TedTalk;
import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class TedTalkRepositoryTest {

  private static final PageRequest PAGE = PageRequest.of(0, 10);
  @Autowired private TedTalkRepository repository;

  @BeforeEach
  void cleanDatabase() {
    repository.deleteAll();
  }

  private static TedTalk talk(String title, String author, int year) {

    return TedTalk.of(
        title,
        author,
        YearMonth.of(year, 1),
        1000,
        100,
        "http://test.com/" + title.replace(" ", "-"));
  }

  @Test
  void findByFilters_shouldReturnAllWhenNoFilters() {
    repository.save(talk("Test Talk", "John Doe", 2020));

    PagedResponse<TedTalk> result = repository.findByFilters(null, null, null, PAGE);

    assertEquals(1, result.metadata().totalElements());
  }

  @Test
  void findByFilters_shouldFilterByAuthor() {
    repository.save(talk("Talk 1", "John Doe", 2020));
    repository.save(talk("Talk 2", "Jane Smith", 2020));

    PagedResponse<TedTalk> result = repository.findByFilters("John", null, null, PAGE);

    assertEquals(1, result.metadata().totalElements());
    assertEquals("John Doe", result.rows().getFirst().getAuthor());
  }

  @Test
  void findByFilters_shouldFilterByYear() {
    repository.save(talk("Talk 1", "John Doe", 2020));
    repository.save(talk("Talk 2", "Jane Smith", 2021));

    PagedResponse<TedTalk> result = repository.findByFilters(null, 2020, null, PAGE);

    assertEquals(1, result.metadata().totalElements());
    assertEquals(2020, result.rows().getFirst().getYearValue());
  }

  @Test
  void findByFilters_shouldFilterByKeyword() {
    repository.save(talk("AI Revolution", "John Doe", 2020));
    repository.save(talk("Climate Change", "Jane Smith", 2020));

    PagedResponse<TedTalk> result = repository.findByFilters(null, null, "AI", PAGE);

    assertEquals(1, result.metadata().totalElements());
    assertTrue(result.rows().getFirst().getTitle().contains("AI"));
  }

  @Test
  void findByFilters_shouldCombineFilters() {
    repository.save(talk("AI Revolution", "John Doe", 2020));

    PagedResponse<TedTalk> result = repository.findByFilters("John", 2020, "AI", PAGE);

    assertEquals(1, result.metadata().totalElements());
    assertEquals("AI Revolution", result.rows().getFirst().getTitle());
  }

  @Test
  void findByFilters_shouldReturnEmptyWhenNoMatch() {
    repository.save(talk("Test Talk", "John Doe", 2020));

    PagedResponse<TedTalk> result = repository.findByFilters("Unknown", null, null, PAGE);

    assertTrue(result.rows().isEmpty());
  }
}
