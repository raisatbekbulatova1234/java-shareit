package ru.practicum.shareit.user.dto;

import lombok.Data;
/**
 * DTO для передачи данных о пользователе при создании или обновлении.
 * Содержит минимально необходимые поля: имя и email.
 */
@Data
public class UserRequestDto {
    private String name;
    private String email;
}
