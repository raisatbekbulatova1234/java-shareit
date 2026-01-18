package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingServiceImpl bookingService;

    /**
     * Создание нового запроса на бронирование
     * POST /bookings
     * Статус после создания: WAITING
     */
    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingDto requestDto) {
        return bookingService.createBooking(requestDto, userId);
    }

    /**
     * Подтверждение/отклонение бронирования владельцем вещи
     * PATCH /bookings/{bookingId}?approved={approved}
     * approved=true → статус APPROVED
     * approved=false → статус REJECTED
     */
    @PatchMapping("/{bookingId}")
    public BookingDto approveOrRejectBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        return bookingService.approveOrRejectBooking(bookingId, ownerId, newStatus);
    }

    /**
     * Получение информации о конкретном бронировании
     * GET /bookings/{bookingId}
     * Доступно: автору бронирования или владельцу вещи
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    /**
     * Получение списка бронирований пользователя (как booker)
     * GET /bookings?state={state}
     * state (опционально): ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED (по умолчанию ALL)
     */
    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(userId, state);
    }

    /**
     * Получение списка бронирований для вещей пользователя (как owner)
     * GET /bookings/owner?state={state}
     * state (опционально): ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED (по умолчанию ALL)
     */
    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }
}
