package com.io.tedtalks.service;

import com.io.tedtalks.config.TedTalksConfig;
import com.io.tedtalks.dto.PagedResponse;
import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.exception.ResourceNotFoundException;
import com.io.tedtalks.model.TedTalk;
import com.io.tedtalks.repository.TedTalkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@code TedTalkService} interface providing the concrete business logic to
 * manage TED Talks.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TedTalkServiceImpl implements TedTalkService {

  private final TedTalkRepository repository;
  private final TedTalksConfig config;

  @Override
  @Transactional
  public TedTalkResponse createTalk(TedTalkRequest request) {
    TedTalk saved = repository.save(TedTalk.of(request));
    log.info("Created TED Talk: {}", saved.getTitle());

    return TedTalkResponse.fromEntity(
        saved, config.influence().viewsWeight(), config.influence().likesWeight());
  }

  @Override
  @Transactional
  public TedTalkResponse updateTalk(Long id, TedTalkRequest request) {
    TedTalk entity = findEntityById(id);

    entity.updateFrom(request);
    repository.save(entity);
    log.info("Updated TED Talk: {}", entity.getId());
    return TedTalkResponse.fromEntity(
        entity, config.influence().viewsWeight(), config.influence().likesWeight());
  }

  @Override
  @Transactional
  public void deleteTalk(Long id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("TED Talk not found with id: " + id);
    }
    repository.deleteById(id);
    log.info("Deleted TED Talk: {}", id);
  }

  @Override
  public TedTalkResponse getTalkById(Long id) {
    TedTalk entity = findEntityById(id);

    return TedTalkResponse.fromEntity(
        entity, config.influence().viewsWeight(), config.influence().likesWeight());
  }

  @Override
  public PagedResponse<TedTalkResponse> getTalks(
      String author, Integer year, String keyword, Pageable pageable) {

    PagedResponse<TedTalk> page = repository.findByFilters(author, year, keyword, pageable);

    List<TedTalkResponse> responses =
        page.rows().stream()
            .map(
                entity ->
                    TedTalkResponse.fromEntity(
                        entity, config.influence().viewsWeight(), config.influence().likesWeight()))
            .toList();

    return new PagedResponse<>(
        responses,
        page.metadata().page(),
        page.metadata().size(),
        page.metadata().totalElements(),
        page.metadata().totalPages());
  }

  @Override
  @Transactional
  public void createTalksBatch(List<TedTalkRequest> requests) {
    List<TedTalk> entities = requests.stream().map(TedTalk::of).toList();
    repository.saveAll(entities);
    log.info("Batch created {} TED Talks", entities.size());
  }

  private TedTalk findEntityById(Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("TED Talk not found with id: " + id));
  }
}
