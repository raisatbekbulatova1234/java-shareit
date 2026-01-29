package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;

import java.util.Map;

/**
 * Клиент для взаимодействия с микросервисом предметов (Items) через REST API.
 * Предоставляет методы для:
 * - создания и обновления предметов;
 * - получения информации о предметах (по ID, по владельцу, по поиску);
 * - добавления комментариев к предметам.
 */
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    /**
     * Конструктор с автоподстановкой зависимостей.
     */
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    /**
     * Создаёт новый предмет.
     */
    public ResponseEntity<Object> createItem(RequestItemDto createItemDto, Long userId) {
        return post("", userId, createItemDto);
    }

    /**
     * Обновляет существующий предмет.
     */
    public ResponseEntity<Object> updateItem(Long itemId, RequestItemDto itemDto, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    /**
     * Получает предмет по его ID.
     */
    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    /**
     * Получает все предметы, принадлежащие указанному владельцу.
     */
    public ResponseEntity<Object> findAllByOwner(Long ownerId) {
        return get("", ownerId);
    }

    /**
     * Ищет предметы по текстовому запросу (по названию или описанию).
     */
    public ResponseEntity<Object> findBySearch(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("?text={text}", null, parameters);
    }

    /**
     * Добавляет комментарий к указанному предмету.
     */
    public ResponseEntity<Object> postComment(CreateCommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
