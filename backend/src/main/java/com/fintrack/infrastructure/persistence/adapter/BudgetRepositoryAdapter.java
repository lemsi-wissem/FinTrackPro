package com.fintrack.infrastructure.persistence.adapter;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.BudgetPeriod;
import com.fintrack.domain.port.out.BudgetRepositoryPort;
import com.fintrack.infrastructure.persistence.mapper.BudgetPersistenceMapper;
import com.fintrack.infrastructure.persistence.repository.JpaBudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BudgetRepositoryAdapter implements BudgetRepositoryPort {

    private final JpaBudgetRepository jpaRepository;
    private final BudgetPersistenceMapper mapper;

    @Override
    public Budget save(Budget b) {
        var entity = mapper.toEntity(b);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Budget> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Budget> findByUserIdAndCategoryIdAndPeriod(UUID userId, UUID categoryId,
                                                                 BudgetPeriod period, int year,
                                                                 Integer month) {
        return jpaRepository.findByUserIdAndCategoryIdAndPeriodAndPeriodYearAndPeriodMonth(
                userId, categoryId, period.name(), year, month).map(mapper::toDomain);
    }

    @Override
    public List<Budget> findByUserIdAndPeriod(UUID userId, BudgetPeriod period, int year, Integer month) {
        return jpaRepository.findByUserIdAndPeriodAndPeriodYearAndPeriodMonth(
                        userId, period.name(), year, month)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Budget> findAllByPeriod(BudgetPeriod period, int year, Integer month) {
        return jpaRepository.findByPeriodAndPeriodYearAndPeriodMonth(period.name(), year, month)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
