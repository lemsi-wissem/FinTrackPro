package com.fintrack.infrastructure.scheduling;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.BudgetPeriod;
import com.fintrack.domain.model.Notification;
import com.fintrack.domain.port.out.BudgetRepositoryPort;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.domain.port.out.NotificationRepositoryPort;
import com.fintrack.domain.port.out.TransactionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
@Slf4j
public class BudgetAlertScheduler {

    private static final String TYPE_BUDGET_80 = "BUDGET_ALERT_80";
    private static final String TYPE_BUDGET_100 = "BUDGET_ALERT_100";

    private final BudgetRepositoryPort budgetRepo;
    private final TransactionRepositoryPort transactionRepo;
    private final NotificationRepositoryPort notificationRepo;
    private final CategoryRepositoryPort categoryRepo;

    /** Runs daily at 8 AM. Checks all monthly budgets for the current month. */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkMonthlyBudgetAlerts() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        YearMonth ym = YearMonth.of(year, month);
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        log.info("Running budget alert check for {}/{}", year, month);

        for (Budget budget : budgetRepo.findAllByPeriod(BudgetPeriod.MONTHLY, year, month)) {
            try {
                checkBudget(budget, from, to, year, month);
            } catch (Exception e) {
                log.error("Error checking budget {}: {}", budget.getId(), e.getMessage());
            }
        }
    }

    private void checkBudget(Budget budget, LocalDate from, LocalDate to, int year, int month) {
        BigDecimal spent = transactionRepo.sumByUserIdAndCategoryAndDateRange(
                budget.getUserId(), budget.getCategoryId(), from, to);

        if (budget.getAmount().compareTo(BigDecimal.ZERO) == 0) return;

        double pct = spent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                .doubleValue() * 100;

        String categoryName = categoryRepo.findById(budget.getCategoryId())
                .map(c -> c.getName()).orElse("Unknown");

        LocalDateTime monthStart = from.atStartOfDay();
        LocalDateTime monthEnd = to.atTime(23, 59, 59);

        if (pct >= 100) {
            boolean alreadySent = notificationRepo.existsByUserIdAndTypeAndReferenceIdAndCreatedAtBetween(
                    budget.getUserId(), TYPE_BUDGET_100, budget.getId(), monthStart, monthEnd);
            if (!alreadySent) {
                notificationRepo.save(Notification.create(
                        budget.getUserId(),
                        TYPE_BUDGET_100,
                        "You have exceeded your " + categoryName + " budget for " +
                                from.getMonth().name().charAt(0) +
                                from.getMonth().name().substring(1).toLowerCase() + ".",
                        budget.getId()));
                log.info("Created 100% alert for budget {} user {}", budget.getId(), budget.getUserId());
            }
        } else if (pct >= 80) {
            boolean alreadySent = notificationRepo.existsByUserIdAndTypeAndReferenceIdAndCreatedAtBetween(
                    budget.getUserId(), TYPE_BUDGET_80, budget.getId(), monthStart, monthEnd);
            if (!alreadySent) {
                notificationRepo.save(Notification.create(
                        budget.getUserId(),
                        TYPE_BUDGET_80,
                        "You've used " + String.format("%.0f", pct) + "% of your " +
                                categoryName + " budget for " +
                                from.getMonth().name().charAt(0) +
                                from.getMonth().name().substring(1).toLowerCase() + ".",
                        budget.getId()));
                log.info("Created 80% alert for budget {} user {}", budget.getId(), budget.getUserId());
            }
        }
    }
}
