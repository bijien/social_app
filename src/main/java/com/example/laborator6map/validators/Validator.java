package com.example.laborator6map.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}