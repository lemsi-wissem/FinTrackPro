package com.fintrack.application.usecase;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.port.in.CreateTransactionUseCase;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

import java.math.BigDecimal;

public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public CreateTransactionUseCaseImpl(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction create(CreateTransactionCommand command) {
        if (command.amount() == null || command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero");
        }
        Transaction transaction = Transaction.create(
                command.userId(), command.categoryId(), command.amount(),
                command.type(), command.description(), command.transactionDate(), command.notes());
        return transactionRepository.save(transaction);
    }
}
