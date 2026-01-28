package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

/**
 * Клиент для взаимодействия с сервисом бронирований (Booking Service).
 * Наследует BaseClient, предоставляя специализированные методы для работы с бронированиями.
 * Использует RestTemplate для выполнения HTTP-запросов.
 */
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings"; // Базовый путь API для бронирований

    /**
     * Конструктор клиента.
     * @param serverUrl Базовый URL удалённого сервиса (из конфигурации)
     * @param builder Строитель RestTemplate для настройки HTTP-клиента
     */
    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        // Устанавливаем базовый URL и префикс API
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        // Используем HttpComponentsClient для HTTP-запросов
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Получает список бронирований пользователя.
     * @param userId ID пользователя (передаётся в заголовке X-Sharer-User-Id)
     * @param state Статус бронирований (ALL, CURRENT и др.)
     * @param from Смещение для пагинации (сколько записей пропустить)
     * @param size Размер страницы (сколько записей вернуть)
     * @return HTTP-ответ с списком бронирований
     */
    public ResponseEntity<Object> getBookings(long userId, BookingState state, Integer from, Integer size) {
        // Формируем параметры запроса
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        // Выполняем GET-запрос с параметрами
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    /**
     * Создаёт новое бронирование.
     * @param userId ID пользователя (в заголовке X-Sharer-User-Id)
     * @param requestDto DTO с данными бронирования (itemId, start, end)
     * @return HTTP-ответ с данными созданного бронирования
     */
    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        // Выполняем POST-запрос с телом requestDto
        return post("", userId, requestDto);
    }

    /**
     * Получает конкретное бронирование по ID.
     * @param userId ID пользователя (в заголовке)
     * @param bookingId ID бронирования
     * @return HTTP-ответ с данными бронирования
     */
    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        // Выполняем GET-запрос по пути /{bookingId}
        return get("/" + bookingId, userId);
    }

    /**
     * Одобряет или отклоняет бронирование.
     * @param userId ID пользователя (владельца ресурса)
     * @param bookingId ID бронирования
     * @param approved true — одобрить, false — отклонить
     * @return HTTP-ответ с результатом операции
     */
    public ResponseEntity<Object> approve(Long userId, Long bookingId, boolean approved) {
        // Формируем параметр запроса
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        // Выполняем PATCH-запрос с параметром approved
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    /**
     * Получает бронирования, связанные с владельцем ресурса.
     * @param userId ID владельца (в заголовке)
     * @param state Статус бронирований
     * @return HTTP-ответ со списком бронирований владельца
     */
    public ResponseEntity<Object> getBookingsByOwner(Long userId, BookingState state) {
        // Формируем параметр состояния
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        // Выполняем GET-запрос по пути /owner с параметром state
        return get("/owner?state={state}", userId, parameters);
    }
}
