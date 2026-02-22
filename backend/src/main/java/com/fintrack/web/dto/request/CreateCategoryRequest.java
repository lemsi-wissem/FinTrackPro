package com.fintrack.web.dto.request;

import com.fintrack.domain.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull TransactionType type,
        @Size(max = 7) String color,
        @Size(max = 50) String icon
) {}
