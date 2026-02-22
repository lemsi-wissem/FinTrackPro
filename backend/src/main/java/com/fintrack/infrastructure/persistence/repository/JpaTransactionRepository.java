package com.fintrack.infrastructure.persistence.repository;

import com.fintrack.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    Optional<TransactionEntity> findByIdAndUserId(UUID id, UUID userId);

    List<TransactionEntity> findTop5ByUserIdOrderByTransactionDateDescCreatedAtDesc(UUID userId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.userId = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:categoryId IS NULL OR t.categoryId = :categoryId) " +
           "AND (:from IS NULL OR t.transactionDate >= :from) " +
           "AND (:to IS NULL OR t.transactionDate <= :to) " +
           "ORDER BY t.transactionDate DESC, t.createdAt DESC")
    List<TransactionEntity> findFiltered(@Param("userId") UUID userId,
                                          @Param("type") String type,
                                          @Param("categoryId") UUID categoryId,
                                          @Param("from") LocalDate from,
                                          @Param("to") LocalDate to,
                                          Pageable pageable);

    @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.userId = :userId " +
           "AND (:type IS NULL OR t.type = :type) " +
           "AND (:categoryId IS NULL OR t.categoryId = :categoryId) " +
           "AND (:from IS NULL OR t.transactionDate >= :from) " +
           "AND (:to IS NULL OR t.transactionDate <= :to)")
    long countFiltered(@Param("userId") UUID userId,
                       @Param("type") String type,
                       @Param("categoryId") UUID categoryId,
                       @Param("from") LocalDate from,
                       @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
           "WHERE t.userId = :userId AND t.type = :type " +
           "AND t.transactionDate BETWEEN :from AND :to")
    BigDecimal sumByUserIdAndTypeAndDateRange(@Param("userId") UUID userId,
                                               @Param("type") String type,
                                               @Param("from") LocalDate from,
                                               @Param("to") LocalDate to);

    @Query("SELECT t.categoryId, SUM(t.amount) FROM TransactionEntity t " +
           "WHERE t.userId = :userId AND t.type = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :from AND :to GROUP BY t.categoryId")
    List<Object[]> sumByCategoryAndDateRange(@Param("userId") UUID userId,
                                              @Param("from") LocalDate from,
                                              @Param("to") LocalDate to);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
           "WHERE t.userId = :userId AND t.categoryId = :categoryId " +
           "AND t.type = 'EXPENSE' AND t.transactionDate BETWEEN :from AND :to")
    BigDecimal sumByUserIdAndCategoryAndDateRange(@Param("userId") UUID userId,
                                                   @Param("categoryId") UUID categoryId,
                                                   @Param("from") LocalDate from,
                                                   @Param("to") LocalDate to);

    @Query("SELECT COUNT(t) > 0 FROM TransactionEntity t WHERE t.userId = :userId " +
           "AND t.transactionDate = :date AND t.amount = :amount " +
           "AND (:description IS NULL AND t.description IS NULL OR t.description = :description)")
    boolean existsDuplicate(@Param("userId") UUID userId,
                             @Param("date") LocalDate date,
                             @Param("amount") BigDecimal amount,
                             @Param("description") String description);
}
