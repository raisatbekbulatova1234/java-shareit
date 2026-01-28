package ru.practicum.shareit.item.dto;

import lombok.Data;

/**
 * DTO для передачи базовых данных о предмете в ответах сервиса.
 * Содержит минимально необходимую информацию: ID, название и ID владельца.
 */
@Data
public class ItemDtoForRequestAnswer {
    private Long id;
    private String name;
    private Long ownerId;
}
