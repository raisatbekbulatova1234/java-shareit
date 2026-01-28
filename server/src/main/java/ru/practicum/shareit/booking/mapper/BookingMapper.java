package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилитарный класс для преобразования между сущностями бронирований и DTO.
 * Обеспечивает:
 * - конвертацию Booking → BookingDto;
 * - конвертацию CreateBookingDto → Booking;
 * - массовую конвертацию коллекций бронирований.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    /**
     * Преобразует сущность Booking в DTO для передачи клиенту.
     */
    public static BookingDto toBookingDto(Booking booking) {
        return booking != null ? new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toResponseItemDto(booking.getItem(), null, null, null),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        ) : null;
    }

    /**
     * Преобразует коллекцию сущностей Booking в список DTO.
     */
    public static List<BookingDto> toBookingDto(Iterable<Booking> bookings) {
        List<BookingDto> res = new ArrayList<>();

        if (bookings != null) {
            bookings.forEach(booking -> res.add(toBookingDto(booking)));
        }

        return res;
    }

    /**
     * Создаёт сущность Booking из DTO создания бронирования.
     * Устанавливает начальный статус WAITING и связывает с Item и User.
     */
    public static Booking toBooking(CreateBookingDto bookingDto, Item item, User user) {
        Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING); // Начальный статус — ожидание подтверждения

        return booking;
    }
}
