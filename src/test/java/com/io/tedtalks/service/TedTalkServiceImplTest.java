package com.io.tedtalks.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.io.tedtalks.config.TedTalksConfig;
import com.io.tedtalks.dto.PagedResponse;
import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.entity.TedTalkEntity;
import com.io.tedtalks.exception.ResourceNotFoundException;
import com.io.tedtalks.repository.TedTalkRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
final class TedTalkServiceImplTest {

  private static final double VIEWS_WEIGHT = 0.7;
  private static final double LIKES_WEIGHT = 0.3;

  @Mock private TedTalkRepository repository;
  @Mock private TedTalksConfig config;
  @Mock private TedTalksConfig.Influence influenceConfig;

  private TedTalkServiceImpl service;

  private static TedTalkRequest request(String title, String author, int year, int month) {

    return new TedTalkRequest(
        title,
        author,
        YearMonth.of(year, month),
        1000,
        100,
        "http://test.com/" + title.replace(" ", "-"));
  }

  private static TedTalkEntity entity(String title) {

    return TedTalkEntity.of(
        title,
        "John Doe",
        YearMonth.of(2020, 1),
        1000,
        100,
        "http://test.com/" + title.replace(" ", "-"));
  }

  @BeforeEach
  void setUp() {
    service = new TedTalkServiceImpl(repository, config);
  }

  private void mockInfluenceConfig() {
    when(config.influence()).thenReturn(influenceConfig);
    when(influenceConfig.viewsWeight()).thenReturn(VIEWS_WEIGHT);
    when(influenceConfig.likesWeight()).thenReturn(LIKES_WEIGHT);
  }

  @Test
  void createTalk_shouldSaveAndReturnTalk() {
    mockInfluenceConfig();

    TedTalkRequest request = request("Test Talk", "John Doe", 2020, 1);
    TedTalkEntity entity = entity("Test Talk");

    when(repository.save(any(TedTalkEntity.class))).thenReturn(entity);

    TedTalkResponse response = service.createTalk(request);

    assertEquals("Test Talk", response.title());
    verify(repository).save(any(TedTalkEntity.class));
  }

  @Test
  void updateTalk_shouldUpdateAndReturnTalk() {
    mockInfluenceConfig();

    TedTalkRequest request = request("Updated Talk", "Jane Doe", 2021, 5);
    TedTalkEntity entity = entity("Old Talk");

    when(repository.findById(1L)).thenReturn(Optional.of(entity));

    TedTalkResponse response = service.updateTalk(1L, request);

    assertNotNull(response);
    assertEquals("Updated Talk", response.title());

    verify(repository).findById(1L);
    verify(repository, never()).save(any());
  }

  @Test
  void updateTalk_shouldThrowExceptionWhenNotFound() {
    TedTalkRequest request = request("Updated Talk", "Jane Doe", 2021, 5);

    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.updateTalk(1L, request));
  }

  @Test
  void deleteTalk_shouldDeleteTalk() {
    when(repository.existsById(1L)).thenReturn(true);

    service.deleteTalk(1L);

    verify(repository).deleteById(1L);
  }

  @Test
  void deleteTalk_shouldThrowExceptionWhenNotFound() {
    when(repository.existsById(1L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> service.deleteTalk(1L));
  }

  @Test
  void getTalkById_shouldReturnTalk() {
    mockInfluenceConfig();

    when(repository.findById(1L)).thenReturn(Optional.of(entity("Test Talk")));

    TedTalkResponse response = service.getTalkById(1L);

    assertEquals("Test Talk", response.title());
  }

  @Test
  void getTalkById_shouldThrowExceptionWhenNotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.getTalkById(1L));
  }

  @Test
  void getTalks_shouldReturnPagedResponse() {
    mockInfluenceConfig();

    Page<TedTalkEntity> page = new PageImpl<>(List.of(entity("Test Talk")));

    when(repository.findByFilters(any(), any(), any(), any(PageRequest.class))).thenReturn(page);

    PagedResponse<TedTalkResponse> response =
        service.getTalks("John", 2020, "test", PageRequest.of(0, 10));

    assertEquals(1, response.rows().size());
    assertEquals("Test Talk", response.rows().get(0).title());
  }
}
