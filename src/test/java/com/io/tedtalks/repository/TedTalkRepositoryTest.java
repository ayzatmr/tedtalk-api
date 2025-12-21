package com.io.tedtalks.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.io.tedtalks.entity.TedTalkEntity;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class TedTalkRepositoryTest {

  private static final PageRequest PAGE = PageRequest.of(0, 10);
  @Autowired private TedTalkRepository repository;

  private static TedTalkEntity talk(String title, String author, int year) {

    return TedTalkEntity.of(
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

    Page<TedTalkEntity> result = repository.findByFilters(null, null, null, PAGE);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void findByFilters_shouldFilterByAuthor() {
    repository.save(talk("Talk 1", "John Doe", 2020));
    repository.save(talk("Talk 2", "Jane Smith", 2020));

    Page<TedTalkEntity> result = repository.findByFilters("John", null, null, PAGE);

    assertEquals(1, result.getTotalElements());
    assertEquals("John Doe", result.getContent().getFirst().getAuthor());
  }

  @Test
  void findByFilters_shouldFilterByYear() {
    repository.save(talk("Talk 1", "John Doe", 2020));
    repository.save(talk("Talk 2", "Jane Smith", 2021));

    Page<TedTalkEntity> result = repository.findByFilters(null, 2020, null, PAGE);

    assertEquals(1, result.getTotalElements());
    assertEquals(2020, result.getContent().getFirst().getYear());
  }

  @Test
  void findByFilters_shouldFilterByKeyword() {
    repository.save(talk("AI Revolution", "John Doe", 2020));
    repository.save(talk("Climate Change", "Jane Smith", 2020));

    Page<TedTalkEntity> result = repository.findByFilters(null, null, "AI", PAGE);

    assertEquals(1, result.getTotalElements());
    assertTrue(result.getContent().getFirst().getTitle().contains("AI"));
  }

  @Test
  void findByFilters_shouldCombineFilters() {
    repository.save(talk("AI Revolution", "John Doe", 2020));

    Page<TedTalkEntity> result = repository.findByFilters("John", 2020, "AI", PAGE);

    assertEquals(1, result.getTotalElements());
    assertEquals("AI Revolution", result.getContent().getFirst().getTitle());
  }

  @Test
  void findByFilters_shouldReturnEmptyWhenNoMatch() {
    repository.save(talk("Test Talk", "John Doe", 2020));

    Page<TedTalkEntity> result = repository.findByFilters("Unknown", null, null, PAGE);

    assertTrue(result.isEmpty());
  }
}
