package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody CreateBookingDto createBookingDto,
                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(createBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                              @PathVariable("bookingId") Long bookingId,
                              @RequestParam("approved") boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") Long bookingId,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingService.findBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsOfCurrentUser(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") States state) {
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwner(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                               @RequestParam(defaultValue = "ALL") States state) {
        return bookingService.getBookingsByOwner(userId, state);
    }
}
