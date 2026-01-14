package ru.practicum.shareit.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.UserNotFoundException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlers {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ErrorResponse("Not Found", ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException ex) {
        String detail = ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Ресурс не найден по указанному идентификатору";
        return new ErrorResponse("Not Found", "Ресурс не найден: " + detail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse("Bad Request", "Некорректный запрос: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder details = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            details.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });
        return new ErrorResponse(
                "Validation Error",
                "Ошибка валидации: " + details.toString().trim()
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ConflictException ex) {
        return new ErrorResponse("Conflict", ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNPE(NullPointerException ex) {
        log.error("NullPointerException: {}", ex.getMessage(), ex);
        return new ErrorResponse(
                "Internal Server Error",
                "Внутренняя ошибка: " + ex.getMessage()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(ForbiddenException ex) {
        return new ErrorResponse("Forbidden", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneralException(Exception ex) {
        log.error("Необработанное исключение: {}, URI: {}", ex.getMessage(), ex);
        return new ErrorResponse(
                "Internal Server Error",
                "Произошла внутренняя ошибка сервера"
        );
    }
}
