package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(CreateBookingDto createBookingDto, Long userId) {
        Long itemId = createBookingDto.getItemId();
        Item bookingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id " + itemId + " не найден"));

        User user = getUser(userId);

        Booking newBooking = BookingValidator.validateBooking(BookingMapper.toBooking(createBookingDto,
                bookingItem, user));

        return BookingMapper.toBookingDto(bookingRepository.save(newBooking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        Booking booking = getBooking(bookingId);

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ConditionsNotMetException("User с id " + userId + " не является владельцем вещи запроса с id "
                    + bookingId);
        }

        booking.setStatus(getStatusByApprove(approved));

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBooking(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);

        User booker = booking.getBooker();
        User itemOwner = booking.getItem().getOwner();

        User requestUser = getUser(userId);

        if (!(booker.equals(requestUser) || itemOwner.equals(requestUser))) {
            throw new ConditionsNotMetException("User с id " + userId
                    + " не является ни автором бронирования ни владельцем вещи запроса с id " + bookingId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, States state) {
        BooleanExpression byState = getExpressionByState(state);
        BooleanExpression byUserId = QBooking.booking.booker.id.eq(userId);

        Iterable<Booking> res = bookingRepository.findAll(byState.and(byUserId));

        return BookingMapper.toBookingDto(res);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, States state) {
        getUser(userId);

        BooleanExpression byOwner = QBooking.booking.item.owner.id.eq(userId);
        BooleanExpression byState = getExpressionByState(state);

        Iterable<Booking> res = bookingRepository.findAll(byOwner.and(byState));
        return BookingMapper.toBookingDto(res);
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking с id " + bookingId + " не найден"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User с id " + id + " не найден"));
    }

    private BookingStatus getStatusByApprove(boolean approved) {
        return approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
    }

    private BooleanExpression getExpressionByState(States state) {
        BooleanExpression approved = QBooking.booking.status.eq(BookingStatus.APPROVED);

        return switch (state) {
            case States.ALL -> Expressions.TRUE;
            case States.CURRENT -> approved
                    .and(QBooking.booking.start.before(LocalDateTime.now()))
                    .and(QBooking.booking.end.after(LocalDateTime.now()));
            case States.FUTURE -> approved.and(QBooking.booking.start.after(LocalDateTime.now()));
            case States.PAST -> approved.and(QBooking.booking.end.before(LocalDateTime.now()));
            case States.WAITING -> QBooking.booking.status.eq(BookingStatus.WAITING);
            case States.REJECTED -> QBooking.booking.status.eq(BookingStatus.REJECTED);
        };
    }
}
