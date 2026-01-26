package ru.practicum.shareit.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.Item.dto.CreateCommentDto;
import ru.practicum.shareit.Item.dto.RequestItemDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(RequestItemDto createItemDto, Long userId) {
        return post("", userId, createItemDto);
    }

    public ResponseEntity<Object> updateItem(Long itemId, RequestItemDto itemDto, Long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> findById(Long itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> findAllByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> findBySearch(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("?text={text}", null, parameters);
    }

    public ResponseEntity<Object> postComment(CreateCommentDto commentDto, Long itemId, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
