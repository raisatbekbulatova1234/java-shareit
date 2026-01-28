package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ResponseItemDto item;
    private UserResponseDto booker;
    private BookingStatus status;
}