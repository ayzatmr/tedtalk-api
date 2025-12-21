package com.io.tedtalks.repository;

import com.io.tedtalks.entity.TedTalkEntity;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface InfluenceAnalysisRepository extends Repository<TedTalkEntity, Long> {}
