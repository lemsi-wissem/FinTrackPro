package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface UpdateTransactionUseCase {
    Transaction update(UpdateTransactionCommand command);

    record UpdateTransactionCommand(UUID transactionId, UUID userId, UUID categoryId,
                                    BigDecimal amount, String description,
                                    LocalDate transactionDate, String notes) {}
}
