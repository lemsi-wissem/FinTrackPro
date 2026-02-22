package com.fintrack.web.controller;

import com.fintrack.domain.model.Category;
import com.fintrack.domain.port.in.CreateCategoryUseCase;
import com.fintrack.domain.port.in.CreateCategoryUseCase.CreateCategoryCommand;
import com.fintrack.domain.port.in.DeleteCategoryUseCase;
import com.fintrack.domain.port.in.GetCategoriesUseCase;
import com.fintrack.domain.port.in.UpdateCategoryUseCase;
import com.fintrack.domain.port.in.UpdateCategoryUseCase.UpdateCategoryCommand;
import com.fintrack.web.dto.request.CreateCategoryRequest;
import com.fintrack.web.dto.request.UpdateCategoryRequest;
import com.fintrack.web.dto.response.CategoryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories")
public class CategoryController {

    private final GetCategoriesUseCase getCategoriesUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<CategoryResponse> categories = getCategoriesUseCase.getForUser(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request,
                                                            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Category category = createCategoryUseCase.create(
                new CreateCategoryCommand(userId, request.name(), request.type(), request.color(), request.icon()));
        return ResponseEntity.status(201).body(toResponse(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id,
                                                            @Valid @RequestBody UpdateCategoryRequest request,
                                                            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Category category = updateCategoryUseCase.update(
                new UpdateCategoryCommand(id, userId, request.name(), request.color(), request.icon()));
        return ResponseEntity.ok(toResponse(category));
    }

    @DeleteMapping("/{id}")
    @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        deleteCategoryUseCase.delete(id, userId);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getUserId(),
                category.getName(),
                category.getType().name(),
                category.getColor(),
                category.getIcon(),
                category.isSystem()
        );
    }
}
