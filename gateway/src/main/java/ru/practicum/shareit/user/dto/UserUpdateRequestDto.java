package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * DTO для частичного обновления данных пользователя (user).
 * Используется при отправке PATCH‑запросов, где допустимо обновление отдельных полей.
 */
@Data
public class UserUpdateRequestDto {


    @Email(message = "Неверная форма email")
    private String email;

    private String name;
}
