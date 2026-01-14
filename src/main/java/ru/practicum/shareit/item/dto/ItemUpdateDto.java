package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemUpdateDto {
    private String name;        // может быть null — не валидируем
    private String description;   // может быть null — не валидируем

    //  @NotNull(message = "Статус доступности не может быть null")
    private Boolean available;

    private Long requestId;
}
