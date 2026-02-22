package com.fintrack.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        String categoryColor,
        BigDecimal amount,
        String type,
        String description,
        LocalDate transactionDate,
        String notes,
        String attachmentUrl,
        LocalDateTime createdAt
) {}
