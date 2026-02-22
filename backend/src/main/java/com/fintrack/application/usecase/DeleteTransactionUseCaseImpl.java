package com.fintrack.application.usecase;

import com.fintrack.domain.exception.TransactionNotFoundException;
import com.fintrack.domain.port.in.DeleteTransactionUseCase;
import com.fintrack.domain.port.out.TransactionRepositoryPort;

import java.util.UUID;

public class DeleteTransactionUseCaseImpl implements DeleteTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;

    public DeleteTransactionUseCaseImpl(TransactionRepositoryPort transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void delete(UUID transactionId, UUID userId) {
        transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        transactionRepository.deleteById(transactionId);
    }
}
