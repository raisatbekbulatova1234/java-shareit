package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        if (booking == null) return null;

        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItem(ItemMapper.toDto(booking.getItem()));
        dto.setBooker(UserMapper.toDto(booking.getBooker()));
        dto.setStatus(booking.getStatus());

        return dto;
    }

    public static Booking toEntity(BookingDto dto) {
        if (dto == null) return null;

        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        // Предполагается, что item и booker уже загружены в сервисе
        // (иначе будет NPE — контроль на уровне сервиса!)
        return booking;
    }
}
