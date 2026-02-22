package com.fintrack.application.usecase;

import com.fintrack.domain.exception.AccessForbiddenException;
import com.fintrack.domain.exception.CategoryNotFoundException;
import com.fintrack.domain.model.Category;
import com.fintrack.domain.port.in.UpdateCategoryUseCase;
import com.fintrack.domain.port.out.CategoryRepositoryPort;

public class UpdateCategoryUseCaseImpl implements UpdateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public UpdateCategoryUseCaseImpl(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category update(UpdateCategoryCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));

        if (category.isSystem()) {
            throw new AccessForbiddenException("Cannot modify system categories");
        }

        if (!command.userId().equals(category.getUserId())) {
            throw new AccessForbiddenException("You do not have permission to modify this category");
        }

        category.update(command.name(), command.color(), command.icon());
        return categoryRepository.save(category);
    }
}
