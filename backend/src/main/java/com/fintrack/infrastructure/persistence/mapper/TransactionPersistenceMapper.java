package com.fintrack.infrastructure.persistence.mapper;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionPersistenceMapper {

    public TransactionEntity toEntity(Transaction domain) {
        return TransactionEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .categoryId(domain.getCategoryId())
                .amount(domain.getAmount())
                .type(domain.getType().name())
                .description(domain.getDescription())
                .transactionDate(domain.getTransactionDate())
                .notes(domain.getNotes())
                .attachmentUrl(domain.getAttachmentUrl())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Transaction toDomain(TransactionEntity entity) {
        return Transaction.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getCategoryId(),
                entity.getAmount(),
                TransactionType.valueOf(entity.getType()),
                entity.getDescription(),
                entity.getTransactionDate(),
                entity.getNotes(),
                entity.getAttachmentUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
