package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Создание новой вещи.
     * @param userId ID пользователя-владельца (из заголовка X-Sharer-User-Id)
     * @param itemDto данные вещи
     * @return созданный DTO вещи с HTTP-статусом 201 (Created)
     */
    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        ItemDto createdItem = itemService.addItem(itemDto, userId);
        return ResponseEntity.status(201).body(createdItem);
    }

    /**
     * Получение базовой информации о вещи (без комментариев и дат бронирований).
     * @param itemId ID вещи
     * @return DTO вещи
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(
            @PathVariable @Positive Long itemId) {

        ItemDto item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }

    /**
     * Получение полной информации о вещи:
     * - базовые данные;
     * - комментарии;
     * - даты последнего и следующего бронирования.
     * @param itemId ID вещи
     * @return DTO с полной информацией
     */
    @GetMapping("/{itemId}/with-comments")
    public ResponseEntity<ItemWithCommentsDto> getItemWithComments(
            @PathVariable @Positive Long itemId) {

        ItemWithCommentsDto item = itemService.getItemWithComments(itemId);
        return ResponseEntity.ok(item);
    }

    /**
     * Частичное обновление вещи (доступно только владельцу).
     * Обновляются только не-null поля из DTO.
     * @param itemId ID вещи
     * @param userId ID пользователя (из заголовка X-Sharer-User-Id)
     * @param updateDto поля для обновления
     * @return обновлённый DTO вещи
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody ItemUpdateDto updateDto) {

        ItemDto updatedItem = itemService.updateItem(itemId, updateDto, userId);
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Удаление вещи (доступно только владельцу).
     * @param itemId ID вещи
     * @param userId ID пользователя (из заголовка X-Sharer-User-Id)
     * @return HTTP-статус 204 (No Content)
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {

        itemService.deleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получение списка вещей владельца (базовая информация).
     * @param userId ID пользователя (из заголовка X-Sharer-User-Id)
     * @return список DTO вещей
     */
    @GetMapping("/owner")
    public ResponseEntity<List<ItemDto>> getOwnerItems(
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {

        List<ItemDto> items = itemService.getItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }

    /**
     * Получение списка вещей владельца с полной информацией:
     * - базовые данные;
     * -комментарии;
     * -даты бронирований.
     * @param userId ID пользователя (из заголовка X-Sharer-User-Id)
     * @return список DTO с полной информацией
     */
    @GetMapping("/owner/with-comments")
    public ResponseEntity<List<ItemWithCommentsDto>> getOwnerItemsWithComments(
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {

        List<ItemWithCommentsDto> items = itemService.getItemsByOwnerWithComments(userId);
        return ResponseEntity.ok(items);
    }

    /**
     * Поиск вещей по тексту (в названии или описании).
     * @param text поисковый запрос
     * @return список найденных DTO вещей
     */
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<ItemDto> results = itemService.searchItems(text.trim());
        return ResponseEntity.ok(results);
    }

    /**
     * Добавление комментария к вещи.
     * @param itemId ID вещи
     * @param userId ID пользователя (из заголовка X-Sharer-User-Id)
     * @param text текст комментария
     * @return созданный DTO комментария с HTTP-статусом 201 (Created)
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody String text) {

        CommentDto comment = itemService.addComment(itemId, userId, text);
        return ResponseEntity.status(201).body(comment);
    }
}
