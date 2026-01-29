package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания/обновления пользователя (user).
 * Используется при отправке POST/PUT‑запросов для передачи данных о новом или обновляемом пользователе.
 */
@Data
public class UserRequestDto {

    @Email(message = "Неверная форма email")
    private String email;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
