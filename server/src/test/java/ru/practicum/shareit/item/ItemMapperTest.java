package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoForRequestAnswer;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Тест-класс для проверки маппера ItemMapper.
 * Проверяет корректность преобразования модели Item в различные DTO:
 * - OwnerItemDto (с информацией о бронированиях);
 * - ItemDtoForRequestAnswer (упрощённое представление).
 */
@SpringBootTest
public class ItemMapperTest {
    private User user;
    private Item item;

    /**
     * Подготовка тестовых данных перед каждым тестом:
     * - создаётся пользователь (владелец предмета);
     * - создаётся предмет с привязанным владельцем.
     */
    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("user");
        user.setEmail("email");

        item = new Item();
        item.setId(1L);
        item.setName("item");
        item.setDescription("description");
        item.setOwner(user);
        item.setAvailable(true);
    }

    /**
     * Тест преобразования Item → OwnerItemDto.
     * Проверяет:
     * - создание не-null объекта OwnerItemDto;
     * - корректное копирование полей из Item;
     * - заполнение полей lastStart/lastEnd и nextStart/nextEnd из бронирований.
     */
    @Test
    public void toOwnerItemDtoTest() {
        // Создаём предыдущее бронирование
        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        lastBooking.setEnd(LocalDateTime.of(2020, 1, 2, 0, 0, 0));
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(BookingStatus.APPROVED);

        // Создаём следующее бронирование
        Booking nextBooking = new Booking();
        nextBooking.setId(2L); // Исправлено: уникальный ID
        nextBooking.setStart(LocalDateTime.of(2020, 1, 3, 0, 0, 0));
        nextBooking.setEnd(LocalDateTime.of(2020, 1, 4, 0, 0, 0));
        nextBooking.setItem(item);
        nextBooking.setBooker(user);
        nextBooking.setStatus(BookingStatus.APPROVED);

        // Преобразуем Item + бронирования в OwnerItemDto
        OwnerItemDto res = ItemMapper.toOwnerItemDto(item, lastBooking, nextBooking);

        // Проверки
        assertThat(res, is(notNullValue())); // Объект создан
        assertThat(res.getId(), is(item.getId())); // ID предмета
        assertThat(res.getName(), is(item.getName())); // Название
        assertThat(res.getDescription(), is(item.getDescription())); // Описание
        assertThat(res.getAvailable(), is(item.getAvailable())); // Доступность
        assertThat(res.getLastStart(), is(lastBooking.getStart())); // Дата начала прошлого бронирования
        assertThat(res.getLastEnd(), is(lastBooking.getEnd())); // Дата окончания прошлого бронирования
        assertThat(res.getNextStart(), is(nextBooking.getStart())); // Дата начала следующего бронирования
        assertThat(res.getNextEnd(), is(nextBooking.getEnd())); // Дата окончания следующего бронирования
    }

    /**
     * Тест преобразования Item → ItemDtoForRequestAnswer.
     * Проверяет:
     * - создание не-null объекта ItemDtoForRequestAnswer;
     * - корректное копирование ID и названия предмета;
     * - получение ID владельца из связанной сущности User.
     */
    @Test
    public void toItemDtoForRequestAnswerTest() {
        // Преобразуем Item в ItemDtoForRequestAnswer
        ItemDtoForRequestAnswer res = ItemMapper.toItemDtoForRequestAnswer(item);


        // Проверки
        assertThat(res, is(notNullValue())); // Объект создан
        assertThat(res.getId(), is(item.getId())); // ID предмета
        assertThat(res.getName(), is(item.getName())); // Название предмета
        assertThat(res.getOwnerId(), is(item.getOwner().getId())); // ID владельца
    }
}
