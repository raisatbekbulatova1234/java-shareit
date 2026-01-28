package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO для создания бронирования (booking).
 * Используется при отправке POST‑запроса для оформления нового бронирования предмета.
 */
@Data
public class CreateBookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}


