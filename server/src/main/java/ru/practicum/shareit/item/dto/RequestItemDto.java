package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * DTO для создания и обновления информации о предмете (item).
 * Используется при отправке POST/PATCH‑запросов для передачи данных о новом или обновляемом предмете.
 */
@Data
@AllArgsConstructor
public class RequestItemDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
