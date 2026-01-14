package ru.practicum.shareit.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String error;      // Краткое название ошибки (например, "Not Found")
    private final String message;    // Подробное описание
}
