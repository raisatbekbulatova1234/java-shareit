package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;

/**
 * Контроллер для обработки HTTP‑запросов, связанных с предметами (items).
 * Обеспечивает endpoints для:
 * - создания/обновления предметов;
 * - получения информации о предметах;
 * - поиска предметов;
 * - добавления комментариев.
 *
 */
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    /**
     * Создаёт новый предмет.
     */
    @PostMapping
    public ResponseEntity<Object> createItem(
            @Valid @RequestBody RequestItemDto createItemDto,
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create Item userId = {}", userId);
        return itemClient.createItem(createItemDto, userId);
    }

    /**
     * Обновляет существующий предмет.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PositiveOrZero @PathVariable("itemId") Long itemId,
            @Valid @RequestBody RequestItemDto itemDto,
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Update Item itemId = {}, userId = {}", itemId, userId);
        return itemClient.updateItem(itemId, itemDto, userId);
    }

    /**
     * Получает предмет по ID.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(
            @PositiveOrZero @PathVariable("itemId") Long itemId) {
        log.info("Get item by id = {}", itemId);
        return itemClient.findById(itemId);
    }

    /**
     * Получает все предметы владельца.
     */
    @GetMapping
    public ResponseEntity<Object> findAllByOwner(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get all items by owner = {}", ownerId);
        return itemClient.findAllByOwner(ownerId);
    }

    /**
     * Ищет предметы по текстовому запросу (по названию или описанию).
     */
    @GetMapping("/search")
    public ResponseEntity<Object> findBySearch(
            @RequestParam("text") String text) {
        log.info("Get all items by search = {}", text);
        return itemClient.findBySearch(text);
    }

    /**
     * Добавляет комментарий к предмету.
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @Valid @RequestBody CreateCommentDto commentDto,
            @PositiveOrZero @PathVariable("itemId") Long itemId,
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Post comment itemId = {}, userId = {}", itemId, userId);
        return itemClient.postComment(commentDto, itemId, userId);
    }
}
