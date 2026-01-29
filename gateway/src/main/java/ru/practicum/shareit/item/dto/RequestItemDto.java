package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для создания/обновления предмета (item).
 * Используется при отправке POST/PUT‑запросов для передачи данных о предмете.
 **/
@Data
@AllArgsConstructor
public class RequestItemDto {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Available не может быть null")
    private Boolean available;
    private Long requestId;
}
