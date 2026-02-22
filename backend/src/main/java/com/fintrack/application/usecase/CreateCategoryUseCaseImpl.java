package com.fintrack.application.usecase;

import com.fintrack.domain.exception.CategoryAlreadyExistsException;
import com.fintrack.domain.model.Category;
import com.fintrack.domain.port.in.CreateCategoryUseCase;
import com.fintrack.domain.port.out.CategoryRepositoryPort;

public class CreateCategoryUseCaseImpl implements CreateCategoryUseCase {

    private final CategoryRepositoryPort categoryRepository;

    public CreateCategoryUseCaseImpl(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(CreateCategoryCommand command) {
        if (categoryRepository.existsByUserIdAndNameAndType(command.userId(), command.name(), command.type())) {
            throw new CategoryAlreadyExistsException(command.name(), command.type().name());
        }
        Category category = Category.create(command.userId(), command.name(), command.type(),
                command.color(), command.icon());
        return categoryRepository.save(category);
    }
}
