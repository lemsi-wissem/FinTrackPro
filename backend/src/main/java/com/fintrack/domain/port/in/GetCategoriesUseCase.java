package com.fintrack.domain.port.in;

import com.fintrack.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface GetCategoriesUseCase {
    List<Category> getForUser(UUID userId);
}
