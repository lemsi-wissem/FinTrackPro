package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface CreateTransactionUseCase {
    Transaction create(CreateTransactionCommand command);

    record CreateTransactionCommand(UUID userId, UUID categoryId, BigDecimal amount,
                                    TransactionType type, String description,
                                    LocalDate transactionDate, String notes) {}
}
