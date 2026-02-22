package com.fintrack.web.controller;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.port.in.GetDashboardSummaryUseCase;
import com.fintrack.domain.port.in.GetDashboardSummaryUseCase.CategorySpending;
import com.fintrack.domain.port.in.GetDashboardSummaryUseCase.DashboardSummary;
import com.fintrack.domain.port.in.GetMonthlyTrendsUseCase;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.web.dto.response.DashboardSummaryResponse;
import com.fintrack.web.dto.response.DashboardSummaryResponse.CategorySpendingDto;
import com.fintrack.web.dto.response.MonthlyTrendsResponse;
import com.fintrack.web.dto.response.TransactionResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final GetDashboardSummaryUseCase getDashboardSummaryUseCase;
    private final GetMonthlyTrendsUseCase getMonthlyTrendsUseCase;
    private final CategoryRepositoryPort categoryRepositoryPort;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        LocalDate now = LocalDate.now();
        int resolvedYear = year != null ? year : now.getYear();
        int resolvedMonth = month != null ? month : now.getMonthValue();

        DashboardSummary summary = getDashboardSummaryUseCase.getSummary(userId, resolvedYear, resolvedMonth);

        List<CategorySpendingDto> topExpenses = summary.topExpensesByCategory().stream()
                .map(this::toCategorySpendingDto)
                .collect(Collectors.toList());

        List<TransactionResponse> recentTransactions = summary.recentTransactions().stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());

        return new DashboardSummaryResponse(
                summary.totalIncome(),
                summary.totalExpenses(),
                summary.netBalance(),
                topExpenses,
                recentTransactions
        );
    }

    @GetMapping("/analytics")
    public ResponseEntity<MonthlyTrendsResponse> getAnalytics(
            @RequestParam int year,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<MonthlyTrendsResponse.MonthlyDataPoint> data = getMonthlyTrendsUseCase.getTrends(userId, year)
                .stream()
                .map(t -> new MonthlyTrendsResponse.MonthlyDataPoint(t.month(), t.income(), t.expenses()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new MonthlyTrendsResponse(data));
    }

    private CategorySpendingDto toCategorySpendingDto(CategorySpending cs) {
        return new CategorySpendingDto(
                cs.categoryId(),
                cs.categoryName(),
                cs.categoryColor(),
                cs.amount(),
                cs.percentage()
        );
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        Optional<Category> category = transaction.getCategoryId() != null
                ? categoryRepositoryPort.findById(transaction.getCategoryId())
                : Optional.empty();
        String categoryName = category.map(Category::getName).orElse(null);
        String categoryColor = category.map(Category::getColor).orElse(null);
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCategoryId(),
                categoryName,
                categoryColor,
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getNotes(),
                transaction.getAttachmentUrl(),
                transaction.getCreatedAt()
        );
    }
}
