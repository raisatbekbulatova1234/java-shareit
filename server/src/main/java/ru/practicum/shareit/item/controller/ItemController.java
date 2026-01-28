package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * Контроллер для обработки HTTP‑запросов, связанных с предметами (items).
 * Обеспечивает endpoints для:
 * - создания новых предметов;
 * - обновления существующих предметов;
 * - получения информации о конкретном предмете;
 * - получения списка предметов владельца;
 * - поиска предметов по тексту;
 * - добавления комментариев к предметам.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    /**
     * Создаёт новый предмет.
     */
    @PostMapping
    public ResponseItemDto create(@RequestBody RequestItemDto createItemDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(createItemDto, userId);
    }

    /**
     * Обновляет существующий предмет.
     */
    @PatchMapping("/{itemId}")
    public ResponseItemDto update(@PathVariable("itemId") Long itemId,
                                  @RequestBody RequestItemDto itemDto,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(itemId, itemDto, userId);
    }

    /**
     * Получает информацию о конкретном предмете.
     */
    @GetMapping("/{itemId}")
    public ResponseItemDto findById(@PathVariable("itemId") Long itemId) {
        return itemService.findById(itemId);
    }

    /**
     * Получает список всех предметов, принадлежащих указанному владельцу.
     */
    @GetMapping
    public List<OwnerItemDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.findAllByOwner(ownerId);
    }

    /**
     * Осуществляет поиск предметов по текстовому запросу.
     */
    @GetMapping("/search")
    public List<ResponseItemDto> findBySearch(@RequestParam("text") String text) {
        return itemService.findBySearch(text);
    }

    /**
     * Добавляет комментарий к указанному предмету.
     */
    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestBody CreateCommentDto commentDto,
                                  @PathVariable("itemId") Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.postComment(commentDto, itemId, userId);
    }
}
