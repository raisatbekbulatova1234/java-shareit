package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // POST /items — добавление новой вещи
    // userId передаётся в заголовке X-Sharer-User-Id
    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {

        ItemDto savedItem = itemService.addItem(itemDto, userId);
        return ResponseEntity.status(201).body(savedItem);
    }

    // PATCH /items/{itemId} — редактирование вещи (только владельцем)
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody ItemDto itemDto) {

        ItemDto updatedItem = itemService.updateItem(itemId, itemDto, userId);
        return ResponseEntity.ok(updatedItem);
    }

    // GET /items/{itemId} — просмотр вещи любым пользователем
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }

    // GET /items — список вещей владельца (userId из заголовка)
    @GetMapping
    public ResponseEntity<List<ItemDto>> getOwnerItems(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        List<ItemDto> items = itemService.getItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }

    // GET /items/search?text={text} — поиск доступных вещей по тексту
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        List<ItemDto> results = itemService.searchItems(text);
        return ResponseEntity.ok(results);
    }

    // DELETE /items/{itemId} — удаление вещи (только владельцем)
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        itemService.deleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }
}
