package com.fintrack.application.usecase;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.BudgetPeriod;
import com.fintrack.domain.port.in.UpsertBudgetUseCase.UpsertBudgetCommand;
import com.fintrack.domain.port.out.BudgetRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpsertBudgetUseCaseImplTest {

    @Mock
    private BudgetRepositoryPort budgetRepository;

    private UpsertBudgetUseCaseImpl useCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new UpsertBudgetUseCaseImpl(budgetRepository);
    }

    @Test
    void upsert_whenNoBudgetExists_shouldCreateAndSaveNewBudget() {
        var command = new UpsertBudgetCommand(userId, categoryId,
                new BigDecimal("500.00"), BudgetPeriod.MONTHLY, 2026, 3);

        when(budgetRepository.findByUserIdAndCategoryIdAndPeriod(
                userId, categoryId, BudgetPeriod.MONTHLY, 2026, 3))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Budget result = useCase.upsert(command);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
        assertThat(result.getAmount()).isEqualByComparingTo("500.00");
        assertThat(result.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(result.getPeriodYear()).isEqualTo(2026);
        assertThat(result.getPeriodMonth()).isEqualTo(3);
        verify(budgetRepository).save(any());
    }

    @Test
    void upsert_whenBudgetExists_shouldUpdateAmountAndSave() {
        var command = new UpsertBudgetCommand(userId, categoryId,
                new BigDecimal("750.00"), BudgetPeriod.MONTHLY, 2026, 3);

        Budget existing = Budget.create(userId, categoryId, new BigDecimal("300.00"),
                BudgetPeriod.MONTHLY, 2026, 3);

        when(budgetRepository.findByUserIdAndCategoryIdAndPeriod(
                userId, categoryId, BudgetPeriod.MONTHLY, 2026, 3))
                .thenReturn(Optional.of(existing));
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Budget result = useCase.upsert(command);

        assertThat(result.getAmount()).isEqualByComparingTo("750.00");
        assertThat(result.getId()).isEqualTo(existing.getId());
        verify(budgetRepository).save(existing);
    }

    @Test
    void upsert_whenBudgetExists_shouldPreserveExistingId() {
        var command = new UpsertBudgetCommand(userId, categoryId,
                new BigDecimal("1000.00"), BudgetPeriod.MONTHLY, 2026, 6);

        Budget existing = Budget.create(userId, categoryId, new BigDecimal("400.00"),
                BudgetPeriod.MONTHLY, 2026, 6);
        UUID existingId = existing.getId();

        when(budgetRepository.findByUserIdAndCategoryIdAndPeriod(any(), any(), any(), anyInt(), any()))
                .thenReturn(Optional.of(existing));
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Budget result = useCase.upsert(command);

        assertThat(result.getId()).isEqualTo(existingId);
    }

    @Test
    void upsert_withYearlyPeriod_shouldCreateWithNullMonth() {
        var command = new UpsertBudgetCommand(userId, categoryId,
                new BigDecimal("12000.00"), BudgetPeriod.YEARLY, 2026, null);

        when(budgetRepository.findByUserIdAndCategoryIdAndPeriod(
                userId, categoryId, BudgetPeriod.YEARLY, 2026, null))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Budget result = useCase.upsert(command);

        assertThat(result.getPeriod()).isEqualTo(BudgetPeriod.YEARLY);
        assertThat(result.getPeriodMonth()).isNull();
        assertThat(result.getAmount()).isEqualByComparingTo("12000.00");
    }

    @Test
    void upsert_savesExactlyOnce() {
        var command = new UpsertBudgetCommand(userId, categoryId,
                new BigDecimal("500.00"), BudgetPeriod.MONTHLY, 2026, 1);

        when(budgetRepository.findByUserIdAndCategoryIdAndPeriod(any(), any(), any(), anyInt(), any()))
                .thenReturn(Optional.empty());
        when(budgetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.upsert(command);

        verify(budgetRepository).save(argThat(b ->
                b.getAmount().compareTo(new BigDecimal("500.00")) == 0));
    }
}
