package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    //Создание нового бронирования.
    BookingDto createBooking(BookingDto requestDto, Long bookerId);

    //Подтверждение или отклонение бронирования владельцем вещи.

    BookingDto approveOrRejectBooking(Long bookingId, Long ownerId, BookingStatus newStatus);

    // Получение бронирования по ID с проверкой прав доступа.

    BookingDto getBookingById(Long bookingId, Long userId);

    //Получение списка бронирований пользователя (как booker).

    List<BookingDto> getUserBookings(Long userId, BookingState state);

    //Получение списка бронирований вещей пользователя (как owner).

    List<BookingDto> getOwnerBookings(Long ownerId, BookingState state);
}
