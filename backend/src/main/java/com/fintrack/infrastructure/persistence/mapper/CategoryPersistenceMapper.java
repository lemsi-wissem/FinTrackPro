package com.fintrack.infrastructure.persistence.mapper;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceMapper {

    public CategoryEntity toEntity(Category domain) {
        return CategoryEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .type(domain.getType().name())
                .color(domain.getColor())
                .icon(domain.getIcon())
                .system(domain.isSystem())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Category toDomain(CategoryEntity entity) {
        return Category.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                TransactionType.valueOf(entity.getType()),
                entity.getColor(),
                entity.getIcon(),
                entity.isSystem(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
