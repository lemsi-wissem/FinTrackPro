package com.fintrack.application.usecase;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.CreateTransactionUseCase.CreateTransactionCommand;
import com.fintrack.domain.port.out.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseImplTest {

    @Mock
    private TransactionRepositoryPort transactionRepository;

    private CreateTransactionUseCaseImpl useCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new CreateTransactionUseCaseImpl(transactionRepository);
    }

    @Test
    void create_whenValidCommand_shouldSaveAndReturnTransaction() {
        var command = new CreateTransactionCommand(
                userId, categoryId, new BigDecimal("250.00"),
                TransactionType.EXPENSE, "Electricity bill",
                LocalDate.of(2026, 1, 15), "Q1 payment");

        Transaction saved = Transaction.create(userId, categoryId,
                new BigDecimal("250.00"), TransactionType.EXPENSE,
                "Electricity bill", LocalDate.of(2026, 1, 15), "Q1 payment");
        when(transactionRepository.save(any())).thenReturn(saved);

        Transaction result = useCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.getAmount()).isEqualByComparingTo("250.00");
        assertThat(result.getType()).isEqualTo(TransactionType.EXPENSE);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void create_whenAmountIsZero_shouldThrowIllegalArgumentException() {
        var command = new CreateTransactionCommand(
                userId, categoryId, BigDecimal.ZERO,
                TransactionType.EXPENSE, "Zero", LocalDate.now(), null);

        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount must be greater than zero");

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void create_whenAmountIsNegative_shouldThrowIllegalArgumentException() {
        var command = new CreateTransactionCommand(
                userId, categoryId, new BigDecimal("-50.00"),
                TransactionType.EXPENSE, "Negative", LocalDate.now(), null);

        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void create_whenAmountIsNull_shouldThrowIllegalArgumentException() {
        var command = new CreateTransactionCommand(
                userId, categoryId, null,
                TransactionType.INCOME, "Null amount", LocalDate.now(), null);

        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void create_incomeTransaction_shouldSaveWithCorrectType() {
        var command = new CreateTransactionCommand(
                userId, null, new BigDecimal("3000.00"),
                TransactionType.INCOME, "Salary", LocalDate.now(), null);

        Transaction saved = Transaction.create(userId, null, new BigDecimal("3000.00"),
                TransactionType.INCOME, "Salary", LocalDate.now(), null);
        when(transactionRepository.save(any())).thenReturn(saved);

        Transaction result = useCase.create(command);

        assertThat(result.getType()).isEqualTo(TransactionType.INCOME);
        verify(transactionRepository, times(1)).save(any());
    }
}
