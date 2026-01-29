package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.CustomHttpHeader;

import java.util.List;

/**
 * Контроллер для управления бронированиями.
 * Обеспечивает REST-интерфейс для:
 * - создания бронирований;
 * - одобрения/отклонения бронирований;
 * - получения информации о бронированиях (для пользователя и владельца);
 * - фильтрации по статусам.
 * Все методы возвращают DTO, готовые для сериализации в JSON.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService; // Сервисная логика бронирований

    /**
     * Создаёт новое бронирование.
     */
    @PostMapping
    public BookingDto createBooking(
             @RequestBody CreateBookingDto createBookingDto,
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        return bookingService.createBooking(createBookingDto, userId);
    }

    /**
     * Одобряет или отклоняет бронирование (только для владельца ресурса).
     */
    @PatchMapping("/{bookingId}")
    public BookingDto approve(
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    /**
     * Получает информацию о конкретном бронировании.
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        return bookingService.findBooking(bookingId, userId);
    }

    /**
     * Получает список бронирований текущего пользователя (арендатора).
     */
    @GetMapping
    public List<BookingDto> getBookingsOfCurrentUser(
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") States state) {
        return bookingService.getBookingsByUser(userId, state);
    }

    /**
     * Получает список бронирований для владельца ресурса.
     */
    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") States state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}
