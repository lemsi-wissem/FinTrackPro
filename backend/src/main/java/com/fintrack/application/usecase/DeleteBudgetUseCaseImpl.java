package com.fintrack.application.usecase;

import com.fintrack.domain.exception.AccessForbiddenException;
import com.fintrack.domain.exception.BudgetNotFoundException;
import com.fintrack.domain.model.Budget;
import com.fintrack.domain.port.in.DeleteBudgetUseCase;
import com.fintrack.domain.port.out.BudgetRepositoryPort;

import java.util.UUID;

public class DeleteBudgetUseCaseImpl implements DeleteBudgetUseCase {

    private final BudgetRepositoryPort budgetRepository;

    public DeleteBudgetUseCaseImpl(BudgetRepositoryPort budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public void delete(UUID budgetId, UUID userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));

        if (!userId.equals(budget.getUserId())) {
            throw new AccessForbiddenException("You do not have permission to delete this budget");
        }

        budgetRepository.deleteById(budgetId);
    }
}
