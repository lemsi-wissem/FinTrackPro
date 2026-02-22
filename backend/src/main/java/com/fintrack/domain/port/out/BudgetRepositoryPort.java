package com.fintrack.domain.port.out;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.BudgetPeriod;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepositoryPort {
    Budget save(Budget b);
    Optional<Budget> findById(UUID id);
    Optional<Budget> findByUserIdAndCategoryIdAndPeriod(UUID userId, UUID categoryId,
                                                         BudgetPeriod period, int year, Integer month);
    List<Budget> findByUserIdAndPeriod(UUID userId, BudgetPeriod period, int year, Integer month);
    List<Budget> findAllByPeriod(BudgetPeriod period, int year, Integer month);
    void deleteById(UUID id);
}
