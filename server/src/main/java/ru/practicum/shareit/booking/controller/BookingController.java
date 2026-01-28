package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
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
 * <p>
 * Все методы возвращают DTO, готовые для сериализации в JSON.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService; // Сервисная логика бронирований


    /**
     * Создаёт новое бронирование.
     *
     * @param createBookingDto DTO с данными для создания бронирования (проверяется валидатором)
     * @param userId           ID пользователя из заголовка X-User-Id
     * @return DTO созданного бронирования
     */
    @PostMapping
    public BookingDto createBooking(
            @Valid @RequestBody CreateBookingDto createBookingDto,
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        return bookingService.createBooking(createBookingDto, userId);
    }

    /**
     * Одобряет или отклоняет бронирование (только для владельца ресурса).
     *
     * @param userId    ID владельца из заголовка X-User-Id
     * @param bookingId ID бронирования из URL
     * @param approved  true — одобрить, false — отклонить
     * @return Обновлённое DTO бронирования
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
     *
     * @param bookingId ID бронирования из URL
     * @param userId    ID пользователя из заголовка X-User-Id (может быть владельцем или арендатором)
     * @return DTO бронирования
     */
    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        return bookingService.findBooking(bookingId, userId);
    }

    /**
     * Получает список бронирований текущего пользователя (арендатора).
     *
     * @param userId ID пользователя из заголовка X-User-Id
     * @param state  Статус бронирований для фильтрации (по умолчанию ALL)
     * @return Список DTO бронирований
     */
    @GetMapping
    public List<BookingDto> getBookingsOfCurrentUser(
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") States state) {
        return bookingService.getBookingsByUser(userId, state);
    }

    /**
     * Получает список бронирований для владельца ресурса.
     *
     * @param userId ID владельца из заголовка X-User-Id
     * @param state  Статус бронирований для фильтрации (по умолчанию ALL)
     * @return Список DTO бронирований
     */
    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(
            @RequestHeader(CustomHttpHeader.USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") States state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}
