package com.fintrack.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Budget {

    private UUID id;
    private UUID userId;
    private UUID categoryId;
    private BigDecimal amount;
    private BudgetPeriod period;
    private int periodYear;
    private Integer periodMonth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Budget() {}

    public static Budget create(UUID userId, UUID categoryId, BigDecimal amount,
                                 BudgetPeriod period, int periodYear, Integer periodMonth) {
        Budget b = new Budget();
        b.id = UUID.randomUUID();
        b.userId = userId;
        b.categoryId = categoryId;
        b.amount = amount;
        b.period = period;
        b.periodYear = periodYear;
        b.periodMonth = periodMonth;
        b.createdAt = LocalDateTime.now();
        b.updatedAt = LocalDateTime.now();
        return b;
    }

    public static Budget reconstitute(UUID id, UUID userId, UUID categoryId, BigDecimal amount,
                                       BudgetPeriod period, int periodYear, Integer periodMonth,
                                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        Budget b = new Budget();
        b.id = id;
        b.userId = userId;
        b.categoryId = categoryId;
        b.amount = amount;
        b.period = period;
        b.periodYear = periodYear;
        b.periodMonth = periodMonth;
        b.createdAt = createdAt;
        b.updatedAt = updatedAt;
        return b;
    }

    public void updateAmount(BigDecimal amount) {
        this.amount = amount;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getCategoryId() { return categoryId; }
    public BigDecimal getAmount() { return amount; }
    public BudgetPeriod getPeriod() { return period; }
    public int getPeriodYear() { return periodYear; }
    public Integer getPeriodMonth() { return periodMonth; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
