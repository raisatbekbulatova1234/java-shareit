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

/**
 * Тест-класс для проверки бизнес-логики сервиса BookingService.
 * Покрывает сценарии создания, утверждения, поиска и фильтрации бронирований.
 */
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImplTest {
    private final EntityManager em;           // Для выполнения JPQL-запросов к БД
    private final BookingService bookingService; // Тестируемый сервис
    private final UserService userService;
    private final ItemService itemService;

    // Фиксированные тестовые даты
    private static final LocalDateTime TEST_START = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final LocalDateTime TEST_END = LocalDateTime.of(2026, 1, 2, 0, 0, 0);

    // ID сущностей для тестов
    private Long userId;        // Владелец предмета
    private Long itemId;       // Предмет для бронирования
    private Long bookerId;    // Пользователь, создающий бронирование
    private Long notOwnerAndBookerId; // Пользователь без прав


    /**
     * Подготовка тестовых данных перед каждым тестом:
     * - создаём 3 пользователя (владелец, бронирующий, нейтральный);
     * - создаём предмет для бронирования.
     */
    @BeforeEach
    public void setUp() {
        // Создаём владельца предмета
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("testUserName");
        userRequestDto.setEmail("testUserEmail");
        userId = userService.create(userRequestDto).getId();


        // Создаём пользователя-брокера
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("requesterName");
        userRequestDto.setEmail("requesterEmail");
        bookerId = userService.create(userRequestDto).getId();

        // Создаём нейтрального пользователя (без прав)
        userRequestDto = new UserRequestDto();
        userRequestDto.setName("userName");
        userRequestDto.setEmail("userEmail");
        notOwnerAndBookerId = userService.create(userRequestDto).getId();

        // Создаём предмет для бронирования
        RequestItemDto requestItemDto = new RequestItemDto(
                "testItemName",
                "testItemDescription",
                true,
                null
        );
        itemId = itemService.create(requestItemDto, userId).getId();
    }

    /**
     * Тест создания бронирования.
     * Проверяет, что бронирование успешно создаётся с корректными полями.
     */
    @Test
    public void createBookingTest() {
        Booking booking = createDefaultBooking();

        assertThat(booking.getId(), notNullValue());           // ID сгенерирован
        assertThat(booking.getStart(), equalTo(TEST_START));    // Дата начала совпадает
        assertThat(booking.getEnd(), equalTo(TEST_END));      // Дата окончания совпадает
        assertThat(booking.getItem().getId(), equalTo(itemId)); // Предмет верный
        assertThat(booking.getBooker().getId(), equalTo(bookerId)); // Бронирующий верный
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING)); // Статус по умолчанию
    }

    /**
     * Тест обработки ошибки: предмет не найден.
     * Проверяет, что при указании несуществующего itemId выбрасывается NotFoundException.
     */
    @Test
    public void handleNotFoundItem() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(TEST_START);
        createBookingDto.setEnd(TEST_END);
        createBookingDto.setItemId(itemId + 1); // Некорректный ID


        Assertions.assertThrows(NotFoundException.class, ()
                -> bookingService.createBooking(createBookingDto, bookerId));
    }

    /**
     * Тест обработки ошибки: пользователь не найден.
     * Проверяет, что при указании несуществующего bookerId выбрасывается NotFoundException.
     */
    @Test
    public void handleNotFoundBooker() {
        CreateBookingDto createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(TEST_START);
        createBookingDto.setEnd(TEST_END);
        createBookingDto.setItemId(itemId);

        Assertions.assertThrows(NotFoundException.class, ()
                -> bookingService.createBooking(createBookingDto, 10L));
    }

    /**
     * Тест обработки ошибки: предмет недоступен.
     * Проверяет, что при попытке забронировать недоступный предмет выбрасывается ConditionsNotMetException.
     */
    @Test
    public void handleItemIsNotAvailable() {
        RequestItemDto requestItemDto = new RequestItemDto(
                "testItemName",
                "testItemDescription",
                false, // Предмет недоступен
                null
        );
        itemId = itemService.create(requestItemDto, userId).getId();

        Assertions.assertThrows(ConditionsNotMetException.class, this::createDefaultBooking);
    }

    /**
     * Тест утверждения бронирования.
     * Проверяет, что статус бронирования меняется на APPROVED после утверждения.
     */
    @Test
    public void approveTest() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        BookingDto res = bookingService.approve(userId, bookId, true);

        assertThat(res.getId(), equalTo(bookId));           // ID сохраняется
        assertThat(res.getStatus(), equalTo(BookingStatus.APPROVED)); // Статус изменён
    }

    /**
     * Тест обработки ошибки: пользователь не владелец.
     * Проверяет, что только владелец может утверждать бронирование.
     */
    @Test
    public void handleUserIsNotOwner() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        Assertions.assertThrows(ConditionsNotMetException.class, ()
                -> bookingService.approve(notOwnerAndBookerId, bookId, true));
    }

    /**
     * Тест обработки ошибки: бронирование не найдено.
     * Проверяет, что при несуществующем ID бронирования выбрасывается NotFoundException.
     */
    @Test
    public void handleBookingNotFound() {
        Assertions.assertThrows(NotFoundException.class, ()
                -> bookingService.approve(userId, 1L, true));
    }

    /**
     * Тест получения бронирования пользователем-брокером.
     * Проверяет, что брокер может получить своё бронирование.
     */
    @Test
    public void findBookingTestByBooker() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        BookingDto res = bookingService.findBooking(bookId, bookerId);

        assertThat(res.getId(), equalTo(bookId)); // ID совпадает
    }
    /**
     * Тест получения бронирования владельцем.
     * Проверяет, что владелец может получить бронирование своего предмета.
     */
    @Test
    public void findBookingTestByOwner() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        BookingDto res = bookingService.findBooking(bookId, userId);

        assertThat(res.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Тест обработки ошибки: пользователь не владелец и не брокер.
     * Проверяет, что посторонний пользователь не может получить бронирование.
     */
    @Test
    public void handleIsNotOwnerOrBooker() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        Assertions.assertThrows(ConditionsNotMetException.class, ()
                -> bookingService.findBooking(bookId, notOwnerAndBookerId));
    }

    /**
     * Тест получения всех бронирований пользователя (по статусу ALL).
     * Проверяет, что возвращается список из 1 бронирования с корректным ID.
     */
    @Test
    public void getBookingsByUser() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.ALL);

        assertThat(res.size(), equalTo(1)); // В списке 1 бронирование
        BookingDto bookRes = res.getFirst();


        assertThat(bookRes.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Тест получения всех бронирований по владельцу (по статусу ALL).
     * Проверяет, что возвращается список из 1 бронирования с корректным ID.
     */
    @Test
    public void getBookingsByOwner() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        List<BookingDto> res = bookingService.getBookingsByOwner(userId, States.ALL);
        assertThat(res.size(), equalTo(1)); // В списке 1 бронирование
        BookingDto bookRes = res.getFirst();


        assertThat(bookRes.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Тест фильтрации текущих бронирований (CURRENT).
     * Проверяет, что бронирование в текущем временном интервале возвращается.
     */
    @Test
    public void shouldFindCurrentBooking() {
        Booking book = createDefaultBooking();
        book.setStart(LocalDateTime.now().minusDays(1));
        book.setEnd(LocalDateTime.now().plusDays(1));
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, true);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.CURRENT);

        assertThat(res.size(), equalTo(1)); // В списке 1 бронирование
        BookingDto bookRes = res.getFirst();


        assertThat(bookRes.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Тест фильтрации будущих бронирований (FUTURE).
     * Проверяет, что бронирование с датой начала в будущем возвращается.
     */
    @Test
    public void shouldFindFutureBookings() {
        Booking book = createDefaultBooking();
        book.setStart(LocalDateTime.now().plusDays(1));
        book.setEnd(LocalDateTime.now().plusDays(2));
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, true);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.FUTURE);

        assertThat(res.size(), equalTo(1)); // В списке 1 бронирование
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Тест фильтрации прошлых бронирований (PAST).
     * Проверяет, что бронирование с прошедшей датой окончания возвращается.
     */
    @Test
    public void shouldFindPastBookings() {
        Booking book = createDefaultBooking();
        book.setStart(LocalDateTime.now().minusDays(2));
        book.setEnd(LocalDateTime.now().minusDays(1));
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, true);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.PAST);

        assertThat(res.size(), equalTo(1)); // В списке 1 бронирование
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Тест фильтрации ожидающих бронирований (WAITING).
     * Проверяет, что бронирование со статусом WAITING возвращается.
     */
    @Test
    public void shouldFindWaiting() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.WAITING);

        assertThat(res.size(), equalTo(1)); // В списке 1 бронирование
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Тест фильтрации отменённых бронирований (REJECTED).
     * Проверяет, что бронирование со статусом REJECTED возвращается.
     */
    @Test
    public void shouldFindCancelledBookings() {
        Booking book = createDefaultBooking();
        Long bookId = book.getId();
        bookingService.approve(userId, bookId, false);

        List<BookingDto> res = bookingService.getBookingsByUser(bookerId, States.REJECTED);

        assertThat(res.size(), equalTo(1)); // В списке 1 бронирование
        BookingDto bookRes = res.getFirst();

        assertThat(bookRes.getId(), equalTo(bookId)); // ID совпадает
    }

    /**
     * Вспомогательный метод для создания бронирования по умолчанию.
     * Используется во многих тестах для подготовки тестовых данных.
     * @return созданное бронирование
     */
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
