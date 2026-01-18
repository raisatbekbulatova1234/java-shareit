package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    @NotNull(message = "Email не может быть null")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotNull(message = "Имя не может быть null")
    private String name;
}
