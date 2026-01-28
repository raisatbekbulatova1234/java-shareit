package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequestAnswer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для передачи данных о запросе на бронирование предмета.
 * Содержит основную информацию о запросе и список подходящих предметов (если есть).
 */
@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoForRequestAnswer> items;
}
