package com.fintrack.application.usecase;

import com.fintrack.domain.exception.TransactionNotFoundException;
import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.port.in.UpdateTransactionUseCase;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

public class UpdateTransactionUseCaseImpl implements UpdateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public UpdateTransactionUseCaseImpl(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction update(UpdateTransactionCommand command) {
        Transaction transaction = transactionRepository.findByIdAndUserId(command.transactionId(), command.userId())
                .orElseThrow(() -> new TransactionNotFoundException(command.transactionId()));

        transaction.update(command.categoryId(), command.amount(), command.description(),
                command.transactionDate(), command.notes());
        return transactionRepository.save(transaction);
    }
}
