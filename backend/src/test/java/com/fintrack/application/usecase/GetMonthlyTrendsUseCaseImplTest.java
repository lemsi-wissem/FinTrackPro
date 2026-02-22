package com.fintrack.application.usecase;

import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.GetMonthlyTrendsUseCase.MonthlyTrend;
import com.fintrack.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMonthlyTrendsUseCaseImplTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    private GetMonthlyTrendsUseCaseImpl useCase;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new GetMonthlyTrendsUseCaseImpl(transactionRepository);
    }

    @Test
    void getTrends_shouldReturn12MonthlyTrendRecords() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<MonthlyTrend> trends = useCase.getTrends(userId, 2026);

        assertThat(trends).hasSize(12);
    }

    @Test
    void getTrends_shouldAssignCorrectMonthNumbers() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<MonthlyTrend> trends = useCase.getTrends(userId, 2026);

        for (int i = 0; i < 12; i++) {
            assertThat(trends.get(i).month()).isEqualTo(i + 1);
        }
    }

    @Test
    void getTrends_shouldCallRepositoryTwicePerMonth() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        useCase.getTrends(userId, 2026);

        // 12 months × 2 types (INCOME + EXPENSE) = 24 calls
        verify(transactionRepository, times(12)).sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.INCOME), any(), any());
        verify(transactionRepository, times(12)).sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.EXPENSE), any(), any());
    }

    @Test
    void getTrends_shouldMapIncomeAndExpensesCorrectly() {
        // Register general stubs FIRST (lower priority), specific stubs LAST (higher priority)
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.INCOME), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.EXPENSE), any(), any()))
                .thenReturn(BigDecimal.ZERO);
        // January 2026 overrides — registered after general stubs so Mockito picks them first
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.INCOME),
                eq(LocalDate.of(2026, 1, 1)), eq(LocalDate.of(2026, 1, 31))))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.EXPENSE),
                eq(LocalDate.of(2026, 1, 1)), eq(LocalDate.of(2026, 1, 31))))
                .thenReturn(new BigDecimal("2000.00"));

        List<MonthlyTrend> trends = useCase.getTrends(userId, 2026);

        MonthlyTrend january = trends.get(0);
        assertThat(january.month()).isEqualTo(1);
        assertThat(january.income()).isEqualByComparingTo("5000.00");
        assertThat(january.expenses()).isEqualByComparingTo("2000.00");
    }

    @Test
    void getTrends_shouldUseCorrectDateBoundariesForEachMonth() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        useCase.getTrends(userId, 2026);

        // Verify February uses correct boundaries (non-leap year: Feb 1 to Feb 28)
        verify(transactionRepository).sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.INCOME),
                eq(LocalDate.of(2026, 2, 1)), eq(LocalDate.of(2026, 2, 28)));
        // Verify December boundaries
        verify(transactionRepository).sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.INCOME),
                eq(LocalDate.of(2026, 12, 1)), eq(LocalDate.of(2026, 12, 31)));
    }

    @Test
    void getTrends_withLeapYear_shouldUseFebruary29() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        useCase.getTrends(userId, 2024); // 2024 is a leap year

        verify(transactionRepository).sumByUserIdAndTypeAndDateRange(
                eq(userId), eq(TransactionType.INCOME),
                eq(LocalDate.of(2024, 2, 1)), eq(LocalDate.of(2024, 2, 29)));
    }

    @Test
    void getTrends_withAllZeroData_shouldReturnZeroValuesForAllMonths() {
        when(transactionRepository.sumByUserIdAndTypeAndDateRange(any(), any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        List<MonthlyTrend> trends = useCase.getTrends(userId, 2026);

        trends.forEach(t -> {
            assertThat(t.income()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(t.expenses()).isEqualByComparingTo(BigDecimal.ZERO);
        });
    }
}
