package com.fintrack.web.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyTrendsResponse(List<MonthlyDataPoint> data) {

    public record MonthlyDataPoint(int month, BigDecimal income, BigDecimal expenses) {}
}
