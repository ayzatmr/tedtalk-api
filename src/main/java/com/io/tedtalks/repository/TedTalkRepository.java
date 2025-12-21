package com.io.tedtalks.repository;

import com.io.tedtalks.entity.TedTalkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing persistent {@code TedTalkEntity} objects. Extends {@link
 * JpaRepository} to provide default CRUD and pagination operations.
 */
@Repository
public interface TedTalkRepository extends JpaRepository<TedTalkEntity, Long> {}
