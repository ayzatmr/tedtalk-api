package com.io.tedtalks.repository;

import com.io.tedtalks.entity.ImportStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link ImportStatusEntity} entities.
 *
 * <p>This interface provides basic CRUD operations and extends the {@link JpaRepository} interface.
 * It is used to interact with the persistent data store for managing the status of import
 * processes.
 */
@Repository
public interface ImportStatusRepository extends JpaRepository<ImportStatusEntity, String> {}
