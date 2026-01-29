package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.States;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreateBookingDto createBookingDto, Long userId);

    BookingDto approve(Long userId, Long bookingId, boolean approved);

    BookingDto findBooking(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUser(Long userId, States state);

    List<BookingDto> getBookingsByOwner(Long userId, States state);
}
