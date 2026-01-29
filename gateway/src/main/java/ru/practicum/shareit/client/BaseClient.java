package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Базовый клиент для взаимодействия с удалёнными сервисами через HTTP.
 * Предоставляет унифицированные методы для выполнения HTTP-запросов (GET, POST, PUT, PATCH, DELETE).
 * Автоматически добавляет необходимые заголовки (Content-Type, Accept, X-Sharer-User-Id).
 */
public class BaseClient {
    protected final RestTemplate rest; // Экземпляр RestTemplate для выполнения HTTP-запросов


    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * Обрабатывает ответ от удалённого сервиса.
     * Если статус ответа — успешный (2xx), возвращает его как есть.
     * Иначе создаёт новый ResponseEntity с тем же статусом и телом (если есть).
     */
    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    // Упрощённые методы GET (без параметров/тела)
    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    protected ResponseEntity<Object> get(String path, long userId) {
        return get(path, userId, null);
    }

    /**
     * Выполняет GET-запрос с указанными параметрами.
     * @param path URL-путь запроса
     * @param userId ID пользователя (добавляется в заголовок X-Sharer-User-Id)
     * @param parameters Параметры запроса (подставляются в URL)
     */
    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    // Аналогично для POST (с телом запроса)
    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return post(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    // Методы для PUT, PATCH, DELETE (аналогично GET/POST)
    protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return put(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId) {
        return patch(path, userId, null, null);
    }

    protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return patch(path, userId, null, body);
    }

    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters, T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    protected ResponseEntity<Object> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    /**
     * Формирует и отправляет HTTP-запрос через RestTemplate.
     * @param method HTTP-метод (GET, POST и т.д.)
     * @param path URL-путь
     * @param userId ID пользователя (для заголовка X-Sharer-User-Id)
     * @param parameters Параметры запроса (подставляются в URL)
     * @param body Тело запроса (для POST/PUT/PATCH)
     * @return Ответ от удалённого сервиса
     */
    private <T> ResponseEntity<Object> makeAndSendRequest(
            HttpMethod method,
            String path,
            Long userId,
            @Nullable Map<String, Object> parameters,
            @Nullable T body) {

        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId)); // Формируем запрос с телом и заголовками

        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                // Отправляем запрос с параметрами
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                // Отправляем запрос без параметров
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            // Обрабатываем ошибки HTTP-статусов (4xx, 5xx)
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    /**
     * Создаёт стандартные заголовки для запроса:
     * - Content-Type: application/json
     * - Accept: application/json
     * - X-Sharer-User-Id: {userId} (если указан)
     */
    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Тип контента
        headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // Ожидаемый тип ответа
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId)); // Добавляем ID пользователя в заголовок
        }
        return headers;
    }
}
