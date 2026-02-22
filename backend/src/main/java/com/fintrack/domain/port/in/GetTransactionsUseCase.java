package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GetTransactionsUseCase {
    List<Transaction> getForUser(UUID userId, TransactionFilter filter);
    long countForUser(UUID userId, TransactionFilter filter);

    record TransactionFilter(
            TransactionType type,
            UUID categoryId,
            LocalDate from,
            LocalDate to,
            int page,
            int size
    ) {}
}
