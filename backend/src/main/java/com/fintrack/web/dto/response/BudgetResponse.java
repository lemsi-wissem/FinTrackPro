package com.fintrack.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        String categoryColor,
        BigDecimal budgetAmount,
        BigDecimal spent,
        BigDecimal remaining,
        double percentageUsed,
        String period,
        int periodYear,
        Integer periodMonth
) {}
