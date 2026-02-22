package com.fintrack.domain.port.in;

import java.util.UUID;

public interface DeleteBudgetUseCase {
    void delete(UUID budgetId, UUID userId);
}
