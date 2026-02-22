package com.fintrack.domain.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GetMonthlyTrendsUseCase {

    List<MonthlyTrend> getTrends(UUID userId, int year);

    record MonthlyTrend(int month, BigDecimal income, BigDecimal expenses) {}
}
