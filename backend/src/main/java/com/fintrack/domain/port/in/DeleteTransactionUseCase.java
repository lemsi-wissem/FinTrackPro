package com.fintrack.domain.port.in;

import java.util.UUID;

public interface DeleteTransactionUseCase {
    void delete(UUID transactionId, UUID userId);
}
