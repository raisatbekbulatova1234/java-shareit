package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания нового комментария к предмету (item).
 * Используется при отправке POST‑запроса на добавление комментария.
 **/
@Data
public class CreateCommentDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
