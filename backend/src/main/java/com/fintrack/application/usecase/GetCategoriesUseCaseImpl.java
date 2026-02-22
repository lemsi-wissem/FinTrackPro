package com.fintrack.application.usecase;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.port.in.GetCategoriesUseCase;
import com.fintrack.domain.port.out.CategoryRepositoryPort;

import java.util.List;
import java.util.UUID;

public class GetCategoriesUseCaseImpl implements GetCategoriesUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public GetCategoriesUseCaseImpl(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getForUser(UUID userId) {
        return categoryRepository.findAllForUser(userId);
    }
}
