package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.model.TransactionType;

import java.util.UUID;

public interface CreateCategoryUseCase {
    Category create(CreateCategoryCommand command);

    record CreateCategoryCommand(UUID userId, String name, TransactionType type, String color, String icon) {}
}
