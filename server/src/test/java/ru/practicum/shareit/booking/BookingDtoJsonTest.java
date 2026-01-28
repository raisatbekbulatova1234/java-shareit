package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Тест-класс для проверки JSON‑сериализации/десериализации DTO бронирований.
 * Использует @JsonTest для автоконфигурации Jackson и JacksonTester для тестирования.
 */
@JsonTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingDtoJsonTest {
    // JacksonTester для CreateBookingDto — позволяет тестировать сериализацию в JSON
    private final JacksonTester<CreateBookingDto> jsonCreateDto;

    // JacksonTester для BookingDto — позволяет тестировать сериализацию в JSON
    private final JacksonTester<BookingDto> jsonDto;

    // Фиксированные тестовые даты для единообразия проверок
    private static final LocalDateTime TEST_START = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final LocalDateTime TEST_END = LocalDateTime.of(2026, 1, 2, 0, 0, 0);


    /**
     * Тест сериализации CreateBookingDto в JSON.
     * Проверяет, что поля DTO корректно преобразуются в JSON.
     */
    @Test
    public void createBookingDtoTest() throws Exception {
        // Создаём экземпляр CreateBookingDto с тестовыми данными
        CreateBookingDto createDto = new CreateBookingDto();
        createDto.setItemId(1L);           // ID предмета
        createDto.setStart(TEST_START);     // Дата начала бронирования
        createDto.setEnd(TEST_END);       // Дата окончания бронирования


        // Сериализуем объект в JSON с помощью JacksonTester
        JsonContent<CreateBookingDto> res = jsonCreateDto.write(createDto);

        // Проверяем, что поле itemId в JSON равно 1
        assertThat(res).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        // Проверяем, что поле start в JSON соответствует тестовой дате
        assertThat(res).extractingJsonPathValue("$.start").isEqualTo("2026-01-01T00:00:00");
        // Проверяем, что поле end в JSON соответствует тестовой дате
        assertThat(res).extractingJsonPathValue("$.end").isEqualTo("2026-01-02T00:00:00");
    }

    /**
     * Тест сериализации BookingDto в JSON.
     * Проверяет, что все поля DTO (включая вложенные объекты) корректно преобразуются в JSON.
     */
    @Test
    public void bookingDtoTest() throws Exception {
        // Создаём тестовый ResponseItemDto (предмет бронирования)
        ResponseItemDto item = new ResponseItemDto(
                1L,                     // ID предмета
                "itemName",           // Название
                "itemDescription",    // Описание
                true,                 // Доступность
                null, null,           // Дополнительные поля (не заполнены)
                null
        );

        // Создаём тестовый UserResponseDto (пользователь, создавший бронирование)
        UserResponseDto user = new UserResponseDto(
                1L,                   // ID пользователя
                "userEmail",          // Email
                "userName"            // Имя
        );

        // Создаём BookingDto с тестовыми данными
        BookingDto dto = new BookingDto(
                1L,                   // ID бронирования
                TEST_START,          // Дата начала
                TEST_END,            // Дата окончания
                item,               // Предмет (вложенный объект)
                user,               // Пользователь (вложенный объект)
                BookingStatus.WAITING  // Статус бронирования
        );

        // Сериализуем объект в JSON с помощью JacksonTester
        JsonContent<BookingDto> res = jsonDto.write(dto);


        // Проверяем поле id в JSON
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        // Проверяем поле start в JSON
        assertThat(res).extractingJsonPathValue("$.start").isEqualTo("2026-01-01T00:00:00");
        // Проверяем поле end в JSON
        assertThat(res).extractingJsonPathValue("$.end").isEqualTo("2026-01-02T00:00:00");


        // Проверяем вложенное поле item.id
        assertThat(res).extractingJsonPathValue("$.item.id").isEqualTo(1);
        // Проверяем вложенное поле item.name
        assertThat(res).extractingJsonPathStringValue("$.item.name").isEqualTo("itemName");
        // Проверяем вложенное поле item.description
        assertThat(res).extractingJsonPathStringValue("$.item.description").isEqualTo("itemDescription");
        // Проверяем вложенное поле item.available
        assertThat(res).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);


        // Проверяем вложенное поле booker.id (ID пользователя)
        assertThat(res).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        // Проверяем вложенное поле booker.name (имя пользователя)
        assertThat(res).extractingJsonPathStringValue("$.booker.name").isEqualTo("userName");
        // Проверяем вложенное поле booker.email (email пользователя)
        assertThat(res).extractingJsonPathStringValue("$.booker.email").isEqualTo("userEmail");

        // Проверяем поле status в JSON (преобразование enum в строку)
        assertThat(res).extractingJsonPathValue("$.status").isEqualTo(BookingStatus.WAITING.toString());
    }
}
