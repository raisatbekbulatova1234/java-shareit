package ru.practicum.shareit.comment.dto;

import lombok.Data;

/**
 * DTO для создания комментария (comment).
 * Используется при отправке POST‑запроса для добавления нового комментария к предмету.
 */
@Data
public class CreateCommentDto {
    private String text;
}
