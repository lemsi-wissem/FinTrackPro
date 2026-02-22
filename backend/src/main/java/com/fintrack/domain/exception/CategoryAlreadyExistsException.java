package com.fintrack.domain.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String name, String type) {
        super("Category with name '" + name + "' and type '" + type + "' already exists");
    }
}
