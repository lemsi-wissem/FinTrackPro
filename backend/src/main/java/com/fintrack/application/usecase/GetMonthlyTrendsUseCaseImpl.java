package com.fintrack.application.usecase;

import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.GetMonthlyTrendsUseCase;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GetMonthlyTrendsUseCaseImpl implements GetMonthlyTrendsUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public GetMonthlyTrendsUseCaseImpl(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<MonthlyTrend> getTrends(UUID userId, int year) {
        List<MonthlyTrend> trends = new ArrayList<>(12);
        for (int month = 1; month <= 12; month++) {
            YearMonth ym = YearMonth.of(year, month);
            LocalDate from = ym.atDay(1);
            LocalDate to = ym.atEndOfMonth();

            BigDecimal income = transactionRepository.sumByUserIdAndTypeAndDateRange(
                    userId, TransactionType.INCOME, from, to);
            BigDecimal expenses = transactionRepository.sumByUserIdAndTypeAndDateRange(
                    userId, TransactionType.EXPENSE, from, to);

            trends.add(new MonthlyTrend(month, income, expenses));
        }
        return trends;
    }
}
