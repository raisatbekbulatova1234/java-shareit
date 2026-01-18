package ru.practicum.shareit.booking.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.PermissionDeniedException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultBookingValidator implements BookingValidator {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void validateCreateBooking(Booking booking) {
        User booker = getUserOrThrow(booking.getBooker().getId());
        Item item = getItemOrThrow(booking.getItem().getId());

        // 1. Запрет бронирования собственной вещи
        if (item.getUserId().equals(booker.getId())) {
            throw new ValidationException("Владелец вещи не может её забронировать");
        }

        // 2. Проверка временных рамок
        validateBookingTimes(booking.getStart(), booking.getEnd());

        // 3. Проверка доступности вещи
        checkItemAvailability(item, booking.getStart(), booking.getEnd());
    }

    @Override
    public void validateApprovalRights(Long bookingId, Long ownerId) {
        Booking booking = getBookingOrThrow(bookingId);
        Item item = booking.getItem();

        if (!item.getUserId().equals(ownerId)) {
            throw new PermissionDeniedException(
                    "Только владелец вещи может подтвердить или отклонить бронирование");
        }

        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new ValidationException("Статус уже установлен и не может быть изменён");
        }
    }

    @Override
    public void validateAccessToBooking(Long bookingId, Long userId) {
        Booking booking = getBookingOrThrow(bookingId);

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getUserId().equals(userId)) {
            throw new PermissionDeniedException(
                    "Доступ запрещён: вы не являетесь автором бронирования или владельцем вещи");
        }
    }

    // Вспомогательные методы

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь", userId));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь", itemId));
    }

    private Booking getBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование", bookingId));
    }

    private void validateBookingTimes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Даты начала и конца бронирования обязательны");
        }
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }
        if (start.isBefore(LocalDateTime.now().minusMinutes(1))) {
            throw new ValidationException("Нельзя бронировать в прошлом");
        }
    }

    private void checkItemAvailability(Item item, LocalDateTime start, LocalDateTime end) {
        List<Booking> activeBookings = bookingRepository
                .findAllByItemIdAndStatus(item.getId(), BookingStatus.APPROVED);

        for (Booking existing : activeBookings) {
            if (isOverlapping(existing.getStart(), existing.getEnd(), start, end)) {
                throw new ValidationException("Вещь уже забронирована на выбранный период");
            }
        }
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
                                  LocalDateTime start2, LocalDateTime end2) {
        return (start1.isBefore(end2) || start1.equals(end2)) &&
                (end1.isAfter(start2) || end1.equals(start2));
    }
}
