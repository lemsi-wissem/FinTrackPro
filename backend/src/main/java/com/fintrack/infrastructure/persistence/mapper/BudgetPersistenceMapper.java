package com.fintrack.infrastructure.persistence.mapper;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.BudgetPeriod;
import com.fintrack.infrastructure.persistence.entity.BudgetEntity;
import org.springframework.stereotype.Component;

@Component
public class BudgetPersistenceMapper {

    public BudgetEntity toEntity(Budget domain) {
        return BudgetEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .categoryId(domain.getCategoryId())
                .amount(domain.getAmount())
                .period(domain.getPeriod().name())
                .periodYear(domain.getPeriodYear())
                .periodMonth(domain.getPeriodMonth())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Budget toDomain(BudgetEntity entity) {
        return Budget.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getCategoryId(),
                entity.getAmount(),
                BudgetPeriod.valueOf(entity.getPeriod()),
                entity.getPeriodYear(),
                entity.getPeriodMonth(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
