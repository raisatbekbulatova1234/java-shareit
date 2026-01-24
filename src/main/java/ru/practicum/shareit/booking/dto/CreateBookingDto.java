package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingDto {
    @NotNull(message = "Время начала бронирования не может быть пустым")
    private LocalDateTime start;

    @NotNull(message = "Время окончания бронирования не может быть пустым")
    private LocalDateTime end;

    @NotNull(message = "Id вещи не может быть пустым")
    @PositiveOrZero(message = "Id вещи не может быть меньше 0")
    private Long itemId;
}


