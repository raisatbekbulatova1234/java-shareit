package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingValidator bookingValidator;

    @Transactional
    public BookingDto createBooking(BookingDto requestDto, Long bookerId) {
        // 1. Загрузка зависимостей
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь", bookerId));
        Item item = itemRepository.findById(requestDto.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь", requestDto.getItem().getId()));

        // 2. Преобразование DTO в сущность
        Booking booking = BookingMapper.toEntity(requestDto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        // 3. Валидация бизнес-правил
        bookingValidator.validateCreateBooking(booking);

        // 4. Сохранение и возврат DTO
        Booking savedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(savedBooking);
    }

    // Подтверждение или отклонение бронирования владельцем вещи
    @Transactional
    public BookingDto approveOrRejectBooking(Long bookingId, Long ownerId, BookingStatus newStatus) {
        bookingValidator.validateApprovalRights(bookingId, ownerId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование", bookingId));

        booking.setStatus(newStatus);
        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.toDto(updatedBooking);
    }

    public BookingDto getBookingById(Long bookingId, Long userId) {
        bookingValidator.validateAccessToBooking(bookingId, userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование", bookingId));

        return BookingMapper.toDto(booking);
    }

    public List<BookingDto> getUserBookings(Long userId, BookingState state) {
        List<Booking> bookings = bookingRepository.findAllByBookerId(userId);
        return filterAndSortBookings(bookings, state);
    }

    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state) {
        List<Booking> bookings = bookingRepository.findAllByItemUserId(ownerId);
        return filterAndSortBookings(bookings, state);
    }

    // Вспомогательные методы

    private List<BookingDto> filterAndSortBookings(List<Booking> bookings, BookingState state) {
        return bookings.stream()
                .filter(booking -> matchesState(booking, state))
                .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart())) // по убыванию start
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean matchesState(Booking booking, BookingState state) {
        return switch (state) {
            case ALL -> true;
            case CURRENT -> {
                LocalDateTime now = LocalDateTime.now();
                yield booking.getStart().isBefore(now) && booking.getEnd().isAfter(now);
            }
            case PAST -> booking.getEnd().isBefore(LocalDateTime.now());
            case FUTURE -> booking.getStart().isAfter(LocalDateTime.now());
            case WAITING -> booking.getStatus() == BookingStatus.WAITING;
            case REJECTED -> booking.getStatus() == BookingStatus.REJECTED;
        };
    }
}
