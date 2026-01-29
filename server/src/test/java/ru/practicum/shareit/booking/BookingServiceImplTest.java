package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.States;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private static final LocalDateTime TEST_START = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final LocalDateTime TEST_END = LocalDateTime.of(2026, 1, 2, 0, 0, 0);
    private Long userId;
    private Long itemId;
    private Long bookerId;
    private Long notOwnerAndBookerId;

    @BeforeEach
    public void setUp() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("testUserName");
        userRequestDto.setEmail("testUserEmail");
        userId = userService.create(userRequestDto).getId();

        userRequestDto = new UserRequestDto();
        userRequestDto.setName("requesterName");
        userRequestDto.setEmail("requesterEmail");
        bookerId = userService.create(userRequestDto).getId();

        userRequestDto = new UserRequestDto();
        userRequestDto.setName("userName");
        userRequestDto.setEmail("userEmail");
        notOwnerAndBookerId = userService.create(userRequestDto).getId();

        RequestItemDto requestItemDto = new RequestItemDto("testItemName",
                "testItemDescription",
                true,
                null);
        itemId = itemService.create(requestItemDto, userId).getId();
    }

    @Test
    public void createBookingTest() {
        Booking booking = createDefaultBooking();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(TEST_START));
        assertThat(booking.getEnd(), equalTo(TEST_END));
        assertThat(booking.getItem().getId(), equalTo(itemId));
        assertThat(booking.getBooker().getId(), equalTo(bookerId));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    public void handleNotFoundItem() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(TEST_START);
        createBookingDto.setEnd(TEST_END);
        createBookingDto.setItemId(itemId + 1);
        Assertions.assertThrows(NotFoundException.class, ()
                -> bookingService.createBooking(createBookingDto, bookerId));
    }

    @Test
    public void handleNotFoundBooker() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(TEST_START);
        createBookingDto.setEnd(TEST_END);
        createBookingDto.setItemId(itemId);
        Assertions.assertThrows(NotFoundException.class, ()
                -> bookingService.createBooking(createBookingDto, 10L));
    }

    @Test
    public void handleItemIsNotAvailable() {
        RequestItemDto requestItemDto = new RequestItemDto("testItemName",
                "testItemDescription",
                false,
                null);
        itemId = itemService.create(requestItemDto, userId).getId();

        Assertions.assertThrows(ConditionsNotMetException.class, this::createDefaultBooking);
    }

    @Test
    public void approveTest() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        BookingDto res = bookingService.approve(userId, bookId, true);

        assertThat(res.getId(), equalTo(bookId));
        assertThat(res.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    public void handleUserIsNotOwner() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        Assertions.assertThrows(ConditionsNotMetException.class, ()
                -> bookingService.approve(notOwnerAndBookerId, bookId, true));
    }

    @Test
    public void handleBookingNotFound() {
        Assertions.assertThrows(NotFoundException.class, ()
                -> bookingService.approve(userId, 1L, true));
    }

    @Test
    public void findBookingTestByBooker() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        BookingDto res = bookingService.findBooking(bookId, bookerId);

        assertThat(res.getId(), equalTo(bookId));
    }

    @Test
    public void findBookingTestByOwner() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        BookingDto res = bookingService.findBooking(bookId, userId);

        assertThat(res.getId(), equalTo(bookId));
    }

    @Test
    public void handleIsNotOwnerOrBooker() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        Assertions.assertThrows(ConditionsNotMetException.class, ()
                -> bookingService.findBooking(bookId, notOwnerAndBookerId));
    }

    @Test
    public void getBookingsByUser() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.ALL);

        assertThat(res.size(), equalTo(1));
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId));
    }

    @Test
    public void getBookingsByOwner() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        List<BookingDto> res = bookingService.getBookingsByOwner(userId, States.ALL);
        assertThat(res.size(), equalTo(1));
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId));
    }

    @Test
    public void shouldFindCurrentBooking() {
        Booking book = createDefaultBooking();
        book.setStart(LocalDateTime.now().minusDays(1));
        book.setEnd(LocalDateTime.now().plusDays(1));
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, true);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.CURRENT);

        assertThat(res.size(), equalTo(1));
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId));
    }

    @Test
    public void shouldFindFutureBookings() {
        Booking book = createDefaultBooking();
        book.setStart(LocalDateTime.now().plusDays(1));
        book.setEnd(LocalDateTime.now().plusDays(2));
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, true);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.FUTURE);

        assertThat(res.size(), equalTo(1));
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId));
    }

    @Test
    public void shouldFindPastBookings() {
        Booking book = createDefaultBooking();
        book.setStart(LocalDateTime.now().minusDays(2));
        book.setEnd(LocalDateTime.now().minusDays(1));
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, true);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.PAST);

        assertThat(res.size(), equalTo(1));
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId));
    }

    @Test
    public void shouldFindWaiting() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.WAITING);

        assertThat(res.size(), equalTo(1));
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId));
    }

    @Test
    public void shouldFindCancelledBookings() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, false);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.REJECTED);

        assertThat(res.size(), equalTo(1));
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId));
    }

    private Booking createDefaultBooking() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(TEST_START);
        createBookingDto.setEnd(TEST_END);
        createBookingDto.setItemId(itemId);
        bookingService.createBooking(createBookingDto, bookerId);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.item.id = :itemId", Booking.class);
        return query.setParameter("itemId", itemId).getSingleResult();
    }
}
