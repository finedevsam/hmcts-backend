package com.core.hmcts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("message", "Validation failed");
        errors.put("status", HttpStatus.BAD_REQUEST.value());

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "rejectedValue", String.valueOf(error.getRejectedValue()),
                        "message", Objects.requireNonNull(error.getDefaultMessage())
                ))
                .collect(Collectors.toList());

        errors.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Map<String, Object>> httpMessageConversionException(HttpMessageConversionException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("message", "Validation failed");
        errors.put("status", HttpStatus.BAD_REQUEST.value());
        errors.put("errors", ex.getMessage());

        return ResponseEntity.badRequest().body(errors);
    }
}
