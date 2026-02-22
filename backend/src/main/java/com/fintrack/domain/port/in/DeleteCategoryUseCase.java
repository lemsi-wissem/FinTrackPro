package com.fintrack.domain.port.in;

import java.util.UUID;

public interface DeleteCategoryUseCase {
    void delete(UUID categoryId, UUID userId);
}
