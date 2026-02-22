package com.fintrack.application.usecase;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.port.in.GetTransactionsUseCase;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

import java.util.List;
import java.util.UUID;

public class GetTransactionsUseCaseImpl implements GetTransactionsUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public GetTransactionsUseCaseImpl(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getForUser(UUID userId, TransactionFilter filter) {
        return transactionRepository.findByUserIdFiltered(userId, filter);
    }

    @Override
    public long countForUser(UUID userId, TransactionFilter filter) {
        return transactionRepository.countByUserIdFiltered(userId, filter);
    }
}
