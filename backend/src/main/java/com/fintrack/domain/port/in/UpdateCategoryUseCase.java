package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Category;

import java.util.UUID;

public interface UpdateCategoryUseCase {
    Category update(UpdateCategoryCommand command);

    record UpdateCategoryCommand(UUID categoryId, UUID userId, String name, String color, String icon) {}
}
