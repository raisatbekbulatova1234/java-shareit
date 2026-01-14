package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemUpdateDto {
    private String name;        // может быть null — не валидируем
    private String description;   // может быть null — не валидируем
    private Boolean available;   // может быть null — не валидируем
    private Long requestId;
}
