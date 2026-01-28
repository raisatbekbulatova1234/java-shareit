package ru.practicum.shareit.user;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

/**
 * Клиент для взаимодействия с API пользователей (users).
 * Обеспечивает отправку HTTP‑запросов к серверу для:
 * - создания новых пользователей;
 * - обновления существующих пользователей;
 * - получения списка всех пользователей;
 * - получения конкретного пользователя по ID;
 * - удаления пользователя по ID.
 */
@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    /**
     * Конструктор с автоподстановкой зависимостей.
     */
    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    /**
     * Создаёт нового пользователя.
     */
    public ResponseEntity<Object> create(UserRequestDto user) {
        return post("", user);
    }

    /**
     * Обновляет существующего пользователя (частичное обновление).
     */
    public ResponseEntity<Object> update(Long userId, UserUpdateRequestDto newUser) {
        return patch("/" + userId, newUser);
    }

    /**
     * Получает список всех пользователей.
     */
    public ResponseEntity<Object> findAll() {
        return get("");
    }

    /**
     * Получает конкретного пользователя по его ID.
     */
    public ResponseEntity<Object> findById(Long id) {
        return get("/" + id);
    }

    /**
     * Удаляет пользователя по его ID.
     */
    public ResponseEntity<Object> deleteById(@PositiveOrZero Long id) {
        return delete("/" + id);
    }
}
