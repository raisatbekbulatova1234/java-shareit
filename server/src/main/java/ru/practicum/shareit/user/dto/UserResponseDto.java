package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для передачи данных о пользователе в ответах API.
 * Содержит базовую информацию о пользователе: ID, email и имя.
 */
@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
}
