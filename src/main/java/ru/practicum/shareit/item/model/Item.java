package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available = true;
    private Long userId; // владелец
    private Long requestId; // ID запроса, по которому создана вещь (может быть null)
}
