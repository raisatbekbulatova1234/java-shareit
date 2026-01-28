package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для передачи подробной информации о предмете в контексте владельца.
 * Содержит расширенные данные, включая временные интервалы последних и следующих бронирований.
 */
@Data
@AllArgsConstructor
public class OwnerItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastStart;
    private LocalDateTime lastEnd;
    private LocalDateTime nextStart;
    private LocalDateTime nextEnd;
}
