package com.fintrack.application.usecase;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.TransactionType;
import java.time.LocalDateTime;
import com.fintrack.domain.port.in.GetDashboardSummaryUseCase.DashboardSummary;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDashboardSummaryUseCaseImplTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    @Mock
    private CategoryRepositoryPort categoryRepository;

    private GetDashboardSummaryUseCaseImpl useCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID catId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new GetDashboardSummaryUseCaseImpl(transactionRepository, categoryRepository);
    }

    @Test
    void getSummary_shouldCalculateNetBalanceCorrectly() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(eq(userId), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(new BigDecimal("3000.00"));
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(eq(userId), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(new BigDecimal("1200.00"));
        when(transactionRepository.sumByCategoryAndDateRange(eq(userId), any(), any()))
                .thenReturn(Collections.emptyMap());
        when(transactionRepository.findTop5ByUserId(userId)).thenReturn(List.of());

        DashboardSummary summary = useCase.getSummary(userId, 2026, 1);

        assertThat(summary.totalIncome()).isEqualByComparingTo("3000.00");
        assertThat(summary.totalExpenses()).isEqualByComparingTo("1200.00");
        assertThat(summary.netBalance()).isEqualByComparingTo("1800.00");
    }

    @Test
    void getSummary_withCategorySpending_shouldCalculatePercentages() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(eq(userId), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(eq(userId), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.sumByCategoryAndDateRange(eq(userId), any(), any()))
                .thenReturn(Map.of(catId, new BigDecimal("400.00")));
        when(transactionRepository.findTop5ByUserId(userId)).thenReturn(List.of());

        Category cat = Category.reconstitute(catId, userId, "Food", TransactionType.EXPENSE,
                "#FF0000", null, false, LocalDateTime.now(), LocalDateTime.now());
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(cat));

        DashboardSummary summary = useCase.getSummary(userId, 2026, 1);

        assertThat(summary.topExpensesByCategory()).hasSize(1);
        assertThat(summary.topExpensesByCategory().get(0).categoryName()).isEqualTo("Food");
        assertThat(summary.topExpensesByCategory().get(0).percentage()).isCloseTo(40.0, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void getSummary_withNoExpenses_shouldReturnZeroPercentageForCategories() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(eq(userId), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(eq(userId), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByCategoryAndDateRange(eq(userId), any(), any()))
                .thenReturn(Map.of(catId, new BigDecimal("0.00")));
        when(transactionRepository.findTop5ByUserId(userId)).thenReturn(List.of());

        Category cat = Category.reconstitute(catId, userId, "Misc", TransactionType.EXPENSE,
                "#AAAAAA", null, false, LocalDateTime.now(), LocalDateTime.now());
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(cat));

        DashboardSummary summary = useCase.getSummary(userId, 2026, 1);

        summary.topExpensesByCategory().forEach(cs ->
                assertThat(cs.percentage()).isEqualTo(0.0));
    }

    @Test
    void getSummary_withUnknownCategory_shouldUseFallbackName() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(new BigDecimal("100.00"));
        when(transactionRepository.sumByCategoryAndDateRange(any(), any(), any()))
                .thenReturn(Map.of(catId, new BigDecimal("100.00")));
        when(transactionRepository.findTop5ByUserId(any())).thenReturn(List.of());
        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        DashboardSummary summary = useCase.getSummary(userId, 2026, 1);

        assertThat(summary.topExpensesByCategory().get(0).categoryName()).isEqualTo("Unknown");
    }

    @Test
    void getSummary_shouldLimitTopExpensesToFive() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(new BigDecimal("700.00"));

        Map<UUID, BigDecimal> spendingMap = new java.util.LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            spendingMap.put(UUID.randomUUID(), new BigDecimal(100 + i));
        }
        when(transactionRepository.sumByCategoryAndDateRange(any(), any(), any())).thenReturn(spendingMap);
        when(transactionRepository.findTop5ByUserId(any())).thenReturn(List.of());
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        DashboardSummary summary = useCase.getSummary(userId, 2026, 1);

        assertThat(summary.topExpensesByCategory()).hasSize(5);
    }
}
