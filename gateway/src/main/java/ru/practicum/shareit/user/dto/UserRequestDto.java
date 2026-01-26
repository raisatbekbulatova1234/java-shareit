package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDto {
    @Email(message = "Неверная форма email")
    private String email;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
