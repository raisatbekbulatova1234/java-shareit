package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

public class BookingValidator {
    public static void validateBooking(BookItemRequestDto booking) {
        validateBookingTime(booking.getStart(), booking.getEnd());
    }

    public static void validateBookingTime(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Начало бронирования " + start
                    + " не может быть позже окончания бронирования " + end);
        }
    }
}
