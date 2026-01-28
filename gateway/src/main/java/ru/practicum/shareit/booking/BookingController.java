package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

/**
 * Контроллер для управления бронированиями.
 * Обрабатывает HTTP-запросы по пути /bookings и делегирует логику BookingClient.
 * Обеспечивает:
 * - валидацию входных данных;
 * - логирование действий;
 * - преобразование параметров запроса в бизнес-объекты.
 */
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient; // Клиент для взаимодействия с сервисом бронирований

    /**
     * Получает список бронирований пользователя с пагинацией и фильтрацией по статусу.
     */
    @GetMapping
    public ResponseEntity<Object> getBookings(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        // Преобразуем строковый параметр в enum BookingState
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

        return bookingClient.getBookings(userId, state, from, size);
    }

    /**
     * Создаёт новое бронирование.
     */
    @PostMapping
    public ResponseEntity<Object> bookItem(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookItemRequestDto requestDto) {

        log.info("Creating booking {}, userId={}", requestDto, userId);

        BookingValidator.validateBooking(requestDto);

        return bookingClient.bookItem(userId, requestDto);
    }

    /**
     * Получает конкретное бронирование по ID.
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @PathVariable Long bookingId) {

        log.info("Get booking {}, userId={}", bookingId, userId);

        return bookingClient.getBooking(userId, bookingId);
    }

    /**
     * Одобряет или отклоняет бронирование.
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @PathVariable("bookingId") Long bookingId,
            @RequestParam("approved") boolean approved) {

        return bookingClient.approve(userId, bookingId, approved);
    }

    /**
     * Получает бронирования, связанные с владельцем ресурса.
     */
    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String stateParam) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

        return bookingClient.getBookingsByOwner(userId, state);
    }
}
