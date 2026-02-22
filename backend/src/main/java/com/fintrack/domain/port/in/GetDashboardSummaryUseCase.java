package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GetDashboardSummaryUseCase {
    DashboardSummary getSummary(UUID userId, int year, int month);

    record DashboardSummary(
            BigDecimal totalIncome,
            BigDecimal totalExpenses,
            BigDecimal netBalance,
            List<CategorySpending> topExpensesByCategory,
            List<Transaction> recentTransactions
    ) {}

    record CategorySpending(UUID categoryId, String categoryName, String categoryColor,
                             BigDecimal amount, double percentage) {}
}
