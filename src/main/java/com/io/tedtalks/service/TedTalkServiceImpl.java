package com.io.tedtalks.service;

import com.io.tedtalks.config.TedTalksConfig;
import com.io.tedtalks.dto.PagedResponse;
import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.dto.TedTalkResponse;
import com.io.tedtalks.entity.TedTalkEntity;
import com.io.tedtalks.exception.ResourceNotFoundException;
import com.io.tedtalks.repository.TedTalkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
  public TedTalkResponse createTalk(TedTalkRequest request) {
    TedTalkEntity saved = repository.save(TedTalkEntity.of(request));
    log.info("Created TED Talk: {}", saved.getTitle());

    return TedTalkResponse.fromEntity(
        saved, config.getInfluence().getViewsWeight(), config.getInfluence().getLikesWeight());
  }

  @Override
  @Transactional
  public TedTalkResponse updateTalk(Long id, TedTalkRequest request) {
    TedTalkEntity entity = findEntityById(id);

    entity.setTitle(request.title());
    entity.setAuthor(request.author());
    entity.setYearMonth(request.date());
    entity.setViews(request.views());
    entity.setLikes(request.likes());
    entity.setLink(request.link());

    TedTalkEntity updated = repository.save(entity);
    log.info("Updated TED Talk: {}", updated.getId());

    return TedTalkResponse.fromEntity(
        updated, config.getInfluence().getViewsWeight(), config.getInfluence().getLikesWeight());
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
    TedTalkEntity entity = findEntityById(id);

    return TedTalkResponse.fromEntity(
        entity, config.getInfluence().getViewsWeight(), config.getInfluence().getLikesWeight());
  }

  @Override
  public PagedResponse<TedTalkResponse> getTalks(
      String author, Integer year, String keyword, Pageable pageable) {

    Page<TedTalkEntity> page = repository.findByFilters(author, year, keyword, pageable);

    return PagedResponse.of(
        page.map(
                entity ->
                    TedTalkResponse.fromEntity(
                        entity,
                        config.getInfluence().getViewsWeight(),
                        config.getInfluence().getLikesWeight()))
            .getContent(),
        page);
  }

  @Override
  @Transactional
  public void createTalksBatch(List<TedTalkRequest> requests) {
    List<TedTalkEntity> entities = requests.stream().map(TedTalkEntity::of).toList();
    repository.saveAll(entities);
    log.info("Batch created {} TED Talks", entities.size());
  }

  private TedTalkEntity findEntityById(Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("TED Talk not found with id: " + id));
  }
}
