package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingStatus status);
    List<Booking> findAllByBookerId(Long bookerId);
    List<Booking> findAllByItemUserId(Long ownerId);
}
