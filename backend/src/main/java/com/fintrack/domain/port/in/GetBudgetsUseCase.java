package com.fintrack.domain.port.in;

import com.fintrack.domain.model.BudgetPeriod;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GetBudgetsUseCase {
    List<BudgetWithSpending> getBudgetsForPeriod(UUID userId, int year, int month);

    record BudgetWithSpending(UUID budgetId, UUID categoryId, String categoryName,
                               String categoryColor, BigDecimal budgetAmount,
                               BigDecimal spent, BigDecimal remaining, double percentageUsed,
                               BudgetPeriod period, int periodYear, Integer periodMonth) {}
}
