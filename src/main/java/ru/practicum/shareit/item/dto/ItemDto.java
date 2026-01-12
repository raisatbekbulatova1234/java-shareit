package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId; // передаём в API
    // Можно добавить только те поля, которые нужно показывать клиенту
    //В DTO нет полей, которые не должны быть видны клиенту
}
