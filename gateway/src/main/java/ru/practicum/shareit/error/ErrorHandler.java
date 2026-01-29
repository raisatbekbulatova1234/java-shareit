package ru.practicum.shareit.error;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST‑контроллеров.
 * Централизованно обрабатывает ошибки:
 * - валидации входных параметров;
 * - нарушений бизнес‑правил.
 * Возвращает структурированный ответ с кодом HTTP 400 (Bad Request).
 */
@RestControllerAdvice
public class ErrorHandler {

    /**
     * Обрабатывает исключения валидации параметров метода контроллера
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        // Собираем ошибки по каждому полю
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Формируем описание: "поле: сообщение; поле: сообщение; ..."
        StringBuilder description = new StringBuilder();
        for (String fieldName : errors.keySet()) {
            description.append(fieldName)
                    .append(": ")
                    .append(errors.get(fieldName))
                    .append("; ");
        }

        return new ErrorResponse("Ошибка валидации", description.toString());
    }

    /**
     * Обрабатывает общие исключения валидации (например, выброшенные вручную).
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handleValidationException(ValidationException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }
}
