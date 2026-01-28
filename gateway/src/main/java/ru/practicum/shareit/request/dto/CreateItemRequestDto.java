package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания запроса на предмет (item request).
 * Используется при отправке POST‑запроса для оформления запроса на аренду/использование предмета.
 */
@Data
public class CreateItemRequestDto {

    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}
