package com.fintrack.infrastructure.persistence.adapter;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.infrastructure.persistence.mapper.CategoryPersistenceMapper;
import com.fintrack.infrastructure.persistence.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final JpaCategoryRepository jpaRepository;
    private final CategoryPersistenceMapper mapper;

    @Override
    public Category save(Category category) {
        var entity = mapper.toEntity(category);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findByUserIdAndName(UUID userId, String name) {
        return jpaRepository.findByUserIdAndNameIgnoreCase(userId, name).map(mapper::toDomain);
    }

    @Override
    public List<Category> findAllForUser(UUID userId) {
        return jpaRepository.findAllForUser(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByUserIdAndNameAndType(UUID userId, String name, TransactionType type) {
        return jpaRepository.existsByUserIdAndNameAndType(userId, name, type.name());
    }
}
