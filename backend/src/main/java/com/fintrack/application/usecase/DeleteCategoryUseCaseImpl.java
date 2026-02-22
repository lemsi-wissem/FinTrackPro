package com.fintrack.application.usecase;

import com.fintrack.domain.exception.AccessForbiddenException;
import com.fintrack.domain.exception.CategoryNotFoundException;
import com.fintrack.domain.model.Category;
import com.fintrack.domain.port.in.DeleteCategoryUseCase;
import com.fintrack.domain.port.out.CategoryRepositoryPort;

import java.util.UUID;

public class DeleteCategoryUseCaseImpl implements DeleteCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public DeleteCategoryUseCaseImpl(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void delete(UUID categoryId, UUID userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        if (category.isSystem()) {
            throw new AccessForbiddenException("Cannot delete system categories");
        }

        if (!userId.equals(category.getUserId())) {
            throw new AccessForbiddenException("You do not have permission to delete this category");
        }

        categoryRepository.deleteById(categoryId);
    }
}
