package com.fintrack.web.dto.request;

import com.fintrack.domain.model.BudgetPeriod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record UpsertBudgetRequest(
        @NotNull UUID categoryId,
        @NotNull @Positive BigDecimal amount,
        @NotNull BudgetPeriod period,
        @NotNull Integer periodYear,
        Integer periodMonth
) {}
