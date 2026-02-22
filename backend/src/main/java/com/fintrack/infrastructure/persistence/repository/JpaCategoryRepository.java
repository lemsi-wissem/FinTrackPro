package com.fintrack.infrastructure.persistence.repository;

import com.fintrack.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    @Query("SELECT c FROM CategoryEntity c WHERE c.userId = :userId OR c.userId IS NULL ORDER BY c.system DESC, c.name ASC")
    List<CategoryEntity> findAllForUser(@Param("userId") UUID userId);

    Optional<CategoryEntity> findByUserIdAndNameIgnoreCase(UUID userId, String name);
    boolean existsByUserIdAndNameAndType(UUID userId, String name, String type);
}
