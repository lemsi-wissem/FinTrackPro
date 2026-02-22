package com.fintrack.web.dto.request;

import com.fintrack.domain.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequest(
        UUID categoryId,
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type,
        @Size(max = 500) String description,
        @NotNull LocalDate transactionDate,
        String notes
) {}
