package com.fintrack.application.usecase;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.port.in.UpsertBudgetUseCase;
import com.fintrack.domain.port.out.BudgetRepositoryPort;

import java.util.Optional;

public class UpsertBudgetUseCaseImpl implements UpsertBudgetUseCase {

    private final BudgetRepositoryPort budgetRepository;

    public UpsertBudgetUseCaseImpl(BudgetRepositoryPort budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public Budget upsert(UpsertBudgetCommand command) {
        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryIdAndPeriod(
                command.userId(), command.categoryId(), command.period(),
                command.periodYear(), command.periodMonth());

        if (existing.isPresent()) {
            Budget budget = existing.get();
            budget.updateAmount(command.amount());
            return budgetRepository.save(budget);
        } else {
            Budget budget = Budget.create(command.userId(), command.categoryId(), command.amount(),
                    command.period(), command.periodYear(), command.periodMonth());
            return budgetRepository.save(budget);
        }
    }
}
