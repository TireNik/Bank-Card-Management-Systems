package ru.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;

@Slf4j
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.warn("404 {}", e.getMessage(), e);
        return new ApiError("NOT_FOUND", "Запрашиваемый ресурс не найден", e.getMessage(), Collections.singletonList(e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        log.warn("409 {}", e.getMessage(), e);
        return new ApiError("CONFLICT", "Конфликт данных", e.getMessage(), Collections.singletonList(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDeniedException(AccessDeniedException e) {
        log.warn("403 FORBIDDEN: {}", e.getMessage(), e);
        return new ApiError("FORBIDDEN", "Доступ запрещён", e.getMessage(),
                List.of(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException e) {
        List<String> validationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        log.warn("400 BAD REQUEST: {}", validationErrors);
        return new ApiError("BAD_REQUEST", "Ошибка валидации данных", "Некорректные параметры запроса", validationErrors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAllUnhandledExceptions(Exception e) {
        log.error("500 INTERNAL SERVER ERROR: {}", e.getMessage(), e);
        return new ApiError("INTERNAL_SERVER_ERROR", "Неожиданная ошибка", e.getMessage(),
                List.of("Произошла непредвиденная ошибка. Попробуйте позже."));
    }
}
