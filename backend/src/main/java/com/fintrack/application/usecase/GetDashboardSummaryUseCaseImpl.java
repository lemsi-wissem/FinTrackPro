package com.fintrack.application.usecase;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.GetDashboardSummaryUseCase;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetDashboardSummaryUseCaseImpl implements GetDashboardSummaryUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final CategoryRepositoryPort categoryRepository;

    public GetDashboardSummaryUseCaseImpl(TransactionRepositoryPort transactionRepository,
                                           CategoryRepositoryPort categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public DashboardSummary getSummary(UUID userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumByUserIdAndTypeAndDateRange(
                userId, TransactionType.INCOME, from, to);
        BigDecimal totalExpenses = transactionRepository.sumByUserIdAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, from, to);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        Map<UUID, BigDecimal> spendingByCategory = transactionRepository.sumByCategoryAndDateRange(userId, from, to);

        List<CategorySpending> topExpenses = spendingByCategory.entrySet().stream()
                .sorted(Map.Entry.<UUID, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(entry -> {
                    Optional<Category> category = categoryRepository.findById(entry.getKey());
                    String categoryName = category.map(Category::getName).orElse("Unknown");
                    String categoryColor = category.map(Category::getColor).orElse(null);
                    double percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                            ? entry.getValue().divide(totalExpenses, 4, RoundingMode.HALF_UP).doubleValue() * 100
                            : 0.0;
                    return new CategorySpending(entry.getKey(), categoryName, categoryColor,
                            entry.getValue(), percentage);
                })
                .collect(Collectors.toList());

        List<Transaction> recentTransactions = transactionRepository.findTop5ByUserId(userId);

        return new DashboardSummary(totalIncome, totalExpenses, netBalance, topExpenses, recentTransactions);
    }
}
