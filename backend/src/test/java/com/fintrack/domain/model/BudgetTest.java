package com.fintrack.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetTest {

    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @Test
    void create_shouldInitializeFieldsAndGenerateId() {
        var amount = new BigDecimal("500.00");

        Budget budget = Budget.create(userId, categoryId, amount,
                BudgetPeriod.MONTHLY, 2026, 3);

        assertThat(budget.getId()).isNotNull();
        assertThat(budget.getUserId()).isEqualTo(userId);
        assertThat(budget.getCategoryId()).isEqualTo(categoryId);
        assertThat(budget.getAmount()).isEqualByComparingTo(amount);
        assertThat(budget.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(budget.getPeriodYear()).isEqualTo(2026);
        assertThat(budget.getPeriodMonth()).isEqualTo(3);
        assertThat(budget.getCreatedAt()).isNotNull();
        assertThat(budget.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_yearlyBudget_shouldAllowNullMonth() {
        Budget budget = Budget.create(userId, categoryId, new BigDecimal("10000.00"),
                BudgetPeriod.YEARLY, 2026, null);

        assertThat(budget.getPeriodMonth()).isNull();
        assertThat(budget.getPeriod()).isEqualTo(BudgetPeriod.YEARLY);
    }

    @Test
    void create_twoBudgets_shouldHaveDifferentIds() {
        Budget b1 = Budget.create(userId, categoryId, BigDecimal.TEN, BudgetPeriod.MONTHLY, 2026, 1);
        Budget b2 = Budget.create(userId, categoryId, BigDecimal.TEN, BudgetPeriod.MONTHLY, 2026, 2);
        assertThat(b1.getId()).isNotEqualTo(b2.getId());
    }

    @Test
    void updateAmount_shouldChangeAmountAndUpdateTimestamp() throws InterruptedException {
        Budget budget = Budget.create(userId, categoryId, new BigDecimal("300.00"),
                BudgetPeriod.MONTHLY, 2026, 1);
        var before = budget.getUpdatedAt();
        Thread.sleep(5);

        budget.updateAmount(new BigDecimal("600.00"));

        assertThat(budget.getAmount()).isEqualByComparingTo("600.00");
        assertThat(budget.getUpdatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void reconstitute_shouldPreserveAllFields() {
        var id = UUID.randomUUID();
        var now = java.time.LocalDateTime.now();

        Budget budget = Budget.reconstitute(id, userId, categoryId,
                new BigDecimal("1000.00"), BudgetPeriod.MONTHLY, 2026, 6, now, now);

        assertThat(budget.getId()).isEqualTo(id);
        assertThat(budget.getAmount()).isEqualByComparingTo("1000.00");
        assertThat(budget.getPeriodMonth()).isEqualTo(6);
        assertThat(budget.getCreatedAt()).isEqualTo(now);
    }
}
