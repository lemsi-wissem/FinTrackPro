package com.fintrack.infrastructure.persistence.adapter;

import com.fintrack.domain.model.Transaction;
import com.fintrack.domain.model.TransactionType;
import com.fintrack.domain.port.in.GetTransactionsUseCase;
import com.fintrack.domain.port.out.TransactionRepositoryPort;
import com.fintrack.infrastructure.persistence.mapper.TransactionPersistenceMapper;
import com.fintrack.infrastructure.persistence.repository.JpaTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final JpaTransactionRepository jpaRepository;
    private final TransactionPersistenceMapper mapper;

    @Override
    public Transaction save(Transaction t) {
        var entity = mapper.toEntity(t);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findByIdAndUserId(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUserId(id, userId).map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findByUserIdFiltered(UUID userId, GetTransactionsUseCase.TransactionFilter filter) {
        String typeStr = filter.type() != null ? filter.type().name() : null;
        var pageable = PageRequest.of(filter.page(), filter.size());
        return jpaRepository.findFiltered(userId, typeStr, filter.categoryId(),
                        filter.from(), filter.to(), pageable)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByUserIdFiltered(UUID userId, GetTransactionsUseCase.TransactionFilter filter) {
        String typeStr = filter.type() != null ? filter.type().name() : null;
        return jpaRepository.countFiltered(userId, typeStr, filter.categoryId(), filter.from(), filter.to());
    }

    @Override
    public List<Transaction> findTop5ByUserId(UUID userId) {
        return jpaRepository.findTop5ByUserIdOrderByTransactionDateDescCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumByUserIdAndTypeAndDateRange(UUID userId, TransactionType type,
                                                      LocalDate from, LocalDate to) {
        BigDecimal result = jpaRepository.sumByUserIdAndTypeAndDateRange(userId, type.name(), from, to);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public Map<UUID, BigDecimal> sumByCategoryAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        List<Object[]> rows = jpaRepository.sumByCategoryAndDateRange(userId, from, to);
        Map<UUID, BigDecimal> result = new HashMap<>();
        for (Object[] row : rows) {
            if (row[0] != null) {
                UUID categoryId = (UUID) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                result.put(categoryId, amount);
            }
        }
        return result;
    }

    @Override
    public BigDecimal sumByUserIdAndCategoryAndDateRange(UUID userId, UUID categoryId,
                                                          LocalDate from, LocalDate to) {
        BigDecimal result = jpaRepository.sumByUserIdAndCategoryAndDateRange(userId, categoryId, from, to);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public boolean existsDuplicate(UUID userId, LocalDate date, BigDecimal amount, String description) {
        return jpaRepository.existsDuplicate(userId, date, amount, description);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
