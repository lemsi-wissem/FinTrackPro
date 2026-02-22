package com.fintrack.domain.port.out;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findByUserIdAndName(UUID userId, String name);
    List<Category> findAllForUser(UUID userId);
    void deleteById(UUID id);
    boolean existsByUserIdAndNameAndType(UUID userId, String name, TransactionType type);
}
