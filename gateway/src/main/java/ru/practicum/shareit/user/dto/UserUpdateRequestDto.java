package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequestDto {
    @Email(message = "Неверная форма email")
    private String email;

    private String name;
}
