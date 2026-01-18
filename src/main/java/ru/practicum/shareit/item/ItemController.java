package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
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

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody ItemDto itemDto) {

        ItemDto createdItem = itemService.addItem(itemDto, userId);
        return ResponseEntity.status(201).body(createdItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable @Positive Long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }


    //Частичное обновление вещи (только владельцем).
    //Обновляются только переданные поля (не null).
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId,
            @Valid @RequestBody ItemUpdateDto updateDto) {

        ItemDto updatedItem = itemService.updateItem(itemId, updateDto, userId);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {

        itemService.deleteItem(itemId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getOwnerItems(
            @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {

        List<ItemDto> items = itemService.getItemsByOwner(userId);
        return ResponseEntity.ok(items);
    }

    //Поиск доступных вещей по тексту (в названии или описании).
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        List<ItemDto> results = itemService.searchItems(text.trim());
        return ResponseEntity.ok(results);
    }
}
