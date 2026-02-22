package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.BudgetPeriod;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpsertBudgetUseCase {
    Budget upsert(UpsertBudgetCommand command);

    record UpsertBudgetCommand(UUID userId, UUID categoryId, BigDecimal amount,
                               BudgetPeriod period, int periodYear, Integer periodMonth) {}
}
