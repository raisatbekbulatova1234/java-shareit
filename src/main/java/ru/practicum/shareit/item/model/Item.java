package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Item {
    private Long id;
    @NotBlank(message = "Название вещи (name) не может быть пустым или содержать только пробелы")
    private String name;

    @NotBlank(message = "Описание вещи (description) не может быть пустым или содержать только пробелы")
    private String description;

    @NotNull(message = "Статус доступности (available) не может быть null")
    private Boolean available;

    @NotNull(message = "ID владельца (userId) не может быть null")
    private Long userId; // владелец
    private Long requestId; // ID запроса, по которому создана вещь (может быть null)
}
