package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тест-класс для проверки BookingController.
 * Использует MockMvc для имитации HTTP-запросов и Mockito для мокирования сервиса.
 */
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper; // Для сериализации/десериализации JSON

    @MockBean
    BookingService bookingService; // Мокированный сервис для изоляции тестов


    @Autowired
    MockMvc mvc; // Инструмент для выполнения HTTP-запросов к контроллеру

    private CreateBookingDto createBookingDto; // DTO для создания бронирования
    private BookingDto bookingDto; // Ожидаемый ответ от контроллера

    /**
     * Подготовка тестовых данных перед каждым тестом.
     */
    @BeforeEach
    public void setup() {
        createBookingDto = new CreateBookingDto();
        createBookingDto.setStart(LocalDateTime.of(2026, 1, 1, 0, 0, 0)); // Дата начала
        createBookingDto.setEnd(LocalDateTime.of(2026, 1, 2, 0, 0, 0));   // Дата окончания
        createBookingDto.setItemId(1L); // ID предмета для бронирования


        bookingDto = new BookingDto(
                1L, // ID бронирования
                createBookingDto.getStart(), // Начало
                createBookingDto.getEnd(), // Окончание
                null, // Пользователь (не заполнен в тесте)
                null, // Предмет (не заполнен в тесте)
                BookingStatus.WAITING // Статус ожидания
        );
    }

    /**
     * Тест создания бронирования (POST /bookings).
     * Проверяет, что при корректных данных возвращается статус 200 OK.
     */
    @Test
    public void createBooking() throws Exception {
        // Мокируем поведение сервиса: при вызове createBooking возвращается bookingDto
        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings") // HTTP-запрос POST
                        .content(mapper.writeValueAsString(createBookingDto)) // Тело запроса (JSON)
                        .header("X-Sharer-User-Id", 1L) // Заголовок с ID пользователя
                        .accept(MediaType.APPLICATION_JSON) // Ожидаемый тип ответа
                        .characterEncoding(StandardCharsets.UTF_8.name()) // Кодировка UTF-8
                        .contentType(MediaType.APPLICATION_JSON)) // Тип контента запроса
                .andExpect(status().isOk()); // Проверка статуса 200 OK
    }

    /**
     * Тест одобрения бронирования (PATCH /bookings/{bookingId}).
     * Проверяет, что при подтверждении бронирования возвращается статус 200 OK.
     */
    @Test
    public void approveTest() throws Exception {
        // Мокируем поведение сервиса: approve возвращает bookingDto
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L) // HTTP-запрос PATCH
                        .header("X-Sharer-User-Id", 1L) // Заголовок с ID пользователя
                        .param("approved", "true") // Параметр запроса (подтверждение)
                        .accept(MediaType.APPLICATION_JSON) // Ожидаемый тип ответа
                        .characterEncoding(StandardCharsets.UTF_8.name()) // Кодировка
                        .contentType(MediaType.APPLICATION_JSON)) // Тип контента
                .andExpect(status().isOk()); // Проверка статуса 200 OK
    }

    /**
     * Тест получения конкретного бронирования (GET /bookings/{bookingId}).
     * Проверяет, что возвращается статус 200 OK.
     */
    @Test
    public void getBookingTest() throws Exception {
        // Мокируем поведение сервиса: findBooking возвращает bookingDto
        when(bookingService.findBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L) // HTTP-запрос GET
                        .header("X-Sharer-User-Id", 1L) // Заголовок с ID пользователя
                        .accept(MediaType.APPLICATION_JSON) // Ожидаемый тип ответа
                        .characterEncoding(StandardCharsets.UTF_8.name()) // Кодировка
                        .contentType(MediaType.APPLICATION_JSON)) // Тип контента
                .andExpect(status().isOk()); // Проверка статуса 200 OK
    }

    /**
     * Тест получения списка бронирований пользователя (GET /bookings).
     * Проверяет:
     * - статус 200 OK;
     * - что в ответе ровно 1 бронирование (jsonPath("$.length()", is(1))).
     */
    @Test
    public void getBookingsOfCurrentUserTest() throws Exception {
        // Мокируем поведение сервиса: getBookingsByUser возвращает список с bookingDto
        when(bookingService.getBookingsByUser(anyLong(), any())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings") // HTTP-запрос GET
                        .header("X-Sharer-User-Id", 1L) // Заголовок с ID пользователя
                        .param("state", "ALL") // Параметр состояния бронирований
                        .accept(MediaType.APPLICATION_JSON) // Ожидаемый тип ответа
                        .characterEncoding(StandardCharsets.UTF_8.name()) // Кодировка
                        .contentType(MediaType.APPLICATION_JSON)) // Тип контента
                .andExpect(status().isOk()) // Проверка статуса 200 OK
                .andExpect(jsonPath("$.length()", is(1))); // Проверка количества элементов
    }

    /**
     * Тест получения бронирований по владельцу (GET /bookings/owner).
     * Проверяет:
     * - статус 200 OK;
     * - что в ответе ровно 1 бронирование.
     */
    @Test
    public void getBookingsByOwner() throws Exception {
        // Мокируем поведение сервиса: getBookingsByOwner возвращает список с bookingDto
        when(bookingService.getBookingsByOwner(anyLong(), any())).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner") // HTTP-запрос GET
                        .header("X-Sharer-User-Id", 1L) // Заголовок с ID пользователя
                        .param("state", "ALL") // Параметр состояния бронирований
                        .accept(MediaType.APPLICATION_JSON) // Ожидаемый тип ответа
                        .characterEncoding(StandardCharsets.UTF_8.name()) // Кодировка
                        .contentType(MediaType.APPLICATION_JSON)) // Тип контента
                .andExpect(status().isOk()) // Проверка статуса 200 OK
                .andExpect(jsonPath("$.length()", is(1))); // Проверка количества элементов
    }
}
