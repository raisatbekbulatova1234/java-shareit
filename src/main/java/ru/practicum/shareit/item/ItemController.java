package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    //POST /items — создание новой вещи
    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        ItemDto createdItem = itemService.addItem(itemDto, userId);
        return ResponseEntity.status(201).body(createdItem);
    }


    //GET /items/{itemId} — получение вещи по ID
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(
            @PathVariable Long itemId) {

        ItemDto item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }


    //PATCH /items/{itemId} — обновление вещи (только владельцем)
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemUpdateDto updateDto) {

        ItemDto updatedItem = itemService.updateItem(itemId, updateDto, userId);
        return ResponseEntity.ok(updatedItem);
    }

    //DELETE /items/{itemId} — удаление вещи (только владельцем)
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        itemService.deleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }


    //GET /items — получение списка вещей владельца
    @GetMapping
    public ResponseEntity<List<ItemDto>> getOwnerItems(
            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        List<ItemDto> items = itemService.getItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }


    //GET /items/search?text={text} — поиск доступных вещей по тексту
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam String text) {

        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<ItemDto> results = itemService.searchItems(text.trim());
        return ResponseEntity.ok(results);
    }
}
