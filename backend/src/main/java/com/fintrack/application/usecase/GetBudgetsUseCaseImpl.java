package com.fintrack.application.usecase;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.BudgetPeriod;
import com.fintrack.domain.model.Category;
import com.fintrack.domain.port.in.GetBudgetsUseCase;
import com.fintrack.domain.port.out.BudgetRepositoryPort;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetBudgetsUseCaseImpl implements GetBudgetsUseCase {

    private final BudgetRepositoryPort budgetRepository;
    private final TransactionRepositoryPort transactionRepository;
    private final CategoryRepositoryPort categoryRepository;

    public GetBudgetsUseCaseImpl(BudgetRepositoryPort budgetRepository,
                                  TransactionRepositoryPort transactionRepository,
                                  CategoryRepositoryPort categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<BudgetWithSpending> getBudgetsForPeriod(UUID userId, int year, int month) {
        List<Budget> budgets = budgetRepository.findByUserIdAndPeriod(userId, BudgetPeriod.MONTHLY, year, month);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();

        Map<UUID, BigDecimal> spendingByCategory = transactionRepository.sumByCategoryAndDateRange(userId, from, to);

        return budgets.stream().map(budget -> {
            BigDecimal spent = spendingByCategory.getOrDefault(budget.getCategoryId(), BigDecimal.ZERO);
            BigDecimal remaining = budget.getAmount().subtract(spent);
            double percentageUsed = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
                    ? spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100
                    : 0.0;

            Optional<Category> category = categoryRepository.findById(budget.getCategoryId());
            String categoryName = category.map(Category::getName).orElse("Unknown");
            String categoryColor = category.map(Category::getColor).orElse(null);

            return new BudgetWithSpending(
                    budget.getId(),
                    budget.getCategoryId(),
                    categoryName,
                    categoryColor,
                    budget.getAmount(),
                    spent,
                    remaining,
                    percentageUsed,
                    budget.getPeriod(),
                    budget.getPeriodYear(),
                    budget.getPeriodMonth()
            );
        }).collect(Collectors.toList());
    }
}
