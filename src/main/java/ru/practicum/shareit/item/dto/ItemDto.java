package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступности не может быть null")
    private Boolean available;
    private Long requestId;

    //даты последнего и следующего бронирования
    private LocalDateTime lastBookingStart;
    private LocalDateTime lastBookingEnd;

    private LocalDateTime nextBookingStart;
    private LocalDateTime nextBookingEnd;
}
