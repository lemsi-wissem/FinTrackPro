package com.fintrack.infrastructure.persistence.repository;

import com.fintrack.infrastructure.persistence.entity.BudgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaBudgetRepository extends JpaRepository<BudgetEntity, UUID> {

    Optional<BudgetEntity> findByUserIdAndCategoryIdAndPeriodAndPeriodYearAndPeriodMonth(
            UUID userId, UUID categoryId, String period, int periodYear, Integer periodMonth);

    List<BudgetEntity> findByUserIdAndPeriodAndPeriodYearAndPeriodMonth(
            UUID userId, String period, int periodYear, Integer periodMonth);

    List<BudgetEntity> findByPeriodAndPeriodYearAndPeriodMonth(
            String period, int periodYear, Integer periodMonth);
}
