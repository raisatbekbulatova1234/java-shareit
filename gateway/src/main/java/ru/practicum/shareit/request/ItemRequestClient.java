package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

/**
 * Клиент для взаимодействия с API запросов на предметы (item requests).
 * Обеспечивает отправку HTTP‑запросов к серверу для:
 * - создания новых запросов;
 * - получения списка запросов пользователя;
 * - получения всех запросов (для административных целей);
 * - получения конкретного запроса по ID.
 */
@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    /**
     * Конструктор с автоподстановкой зависимостей.
     */
    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    /**
     * Создаёт новый запрос на предмет.
     */
    public ResponseEntity<Object> create(CreateItemRequestDto createItemRequestDto, Long requesterId) {
        return post("", requesterId, createItemRequestDto);
    }

    /**
     * Получает все запросы, созданные указанным пользователем.
     */
    public ResponseEntity<Object> getItemRequestsByUser(Long requesterId) {
        return get("", requesterId);
    }

    /**
     * Получает полный список всех запросов (вероятно, для административных целей).
     */
    public ResponseEntity<Object> getAll() {
        return get("/all");
    }

    /**
     * Получает конкретный запрос по его ID.
     */
    public ResponseEntity<Object> getItemRequestById(Long requestId) {
        return get("/" + requestId);
    }
}
