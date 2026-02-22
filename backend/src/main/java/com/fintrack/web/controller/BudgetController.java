package com.fintrack.web.controller;

import com.fintrack.domain.model.Budget;
import com.fintrack.domain.model.Category;
import com.fintrack.domain.port.in.DeleteBudgetUseCase;
import com.fintrack.domain.port.in.GetBudgetsUseCase;
import com.fintrack.domain.port.in.GetBudgetsUseCase.BudgetWithSpending;
import com.fintrack.domain.port.in.UpsertBudgetUseCase;
import com.fintrack.domain.port.in.UpsertBudgetUseCase.UpsertBudgetCommand;
import com.fintrack.domain.port.out.CategoryRepositoryPort;
import com.fintrack.web.dto.request.UpsertBudgetRequest;
import com.fintrack.web.dto.response.BudgetResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
@Tag(name = "Budgets")
public class BudgetController {

    private final GetBudgetsUseCase getBudgetsUseCase;
    private final UpsertBudgetUseCase upsertBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;
    private final CategoryRepositoryPort categoryRepositoryPort;

    @GetMapping
    public List<BudgetResponse> getBudgets(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return getBudgetsUseCase.getBudgetsForPeriod(userId, year, month).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PutMapping
    public BudgetResponse upsertBudget(@Valid @RequestBody UpsertBudgetRequest request,
                                        Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Budget budget = upsertBudgetUseCase.upsert(new UpsertBudgetCommand(
                userId, request.categoryId(), request.amount(),
                request.period(), request.periodYear(), request.periodMonth()));

        Optional<Category> category = categoryRepositoryPort.findById(budget.getCategoryId());
        String categoryName = category.map(Category::getName).orElse(null);
        String categoryColor = category.map(Category::getColor).orElse(null);

        return new BudgetResponse(
                budget.getId(),
                budget.getCategoryId(),
                categoryName,
                categoryColor,
                budget.getAmount(),
                BigDecimal.ZERO,
                budget.getAmount(),
                0.0,
                budget.getPeriod().name(),
                budget.getPeriodYear(),
                budget.getPeriodMonth()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBudget(@PathVariable UUID id, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        deleteBudgetUseCase.delete(id, userId);
    }

    private BudgetResponse toResponse(BudgetWithSpending b) {
        return new BudgetResponse(
                b.budgetId(),
                b.categoryId(),
                b.categoryName(),
                b.categoryColor(),
                b.budgetAmount(),
                b.spent(),
                b.remaining(),
                b.percentageUsed(),
                b.period() != null ? b.period().name() : null,
                b.periodYear(),
                b.periodMonth()
        );
    }
}
