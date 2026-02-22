package com.fintrack.web.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record DashboardSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance,
        List<CategorySpendingDto> topExpensesByCategory,
        List<TransactionResponse> recentTransactions
) {
    public record CategorySpendingDto(
            UUID categoryId,
            String categoryName,
            String categoryColor,
            BigDecimal amount,
            double percentage
    ) {}
}
