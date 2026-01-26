package ru.practicum.shareit.Item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
