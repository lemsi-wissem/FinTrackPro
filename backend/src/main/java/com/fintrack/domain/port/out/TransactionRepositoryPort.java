package com.fintrack.domain.port.out;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.GetTransactionsUseCase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {
    Transaction save(Transaction t);
    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
    List<Transaction> findByUserIdFiltered(UUID userId, GetTransactionsUseCase.TransactionFilter filter);
    long countByUserIdFiltered(UUID userId, GetTransactionsUseCase.TransactionFilter filter);
    List<Transaction> findTop5ByUserId(UUID userId);
    BigDecimal sumByUserIdAndTypeAndDateRange(UUID userId, TransactionType type, LocalDate from, LocalDate to);
    BigDecimal sumByUserIdAndCategoryAndDateRange(UUID userId, UUID categoryId, LocalDate from, LocalDate to);
    Map<UUID, BigDecimal> sumByCategoryAndDateRange(UUID userId, LocalDate from, LocalDate to);
    boolean existsDuplicate(UUID userId, LocalDate date, BigDecimal amount, String description);
    void deleteById(UUID id);
}
