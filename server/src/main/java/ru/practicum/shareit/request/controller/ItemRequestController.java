package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomHttpHeader;

import java.util.List;

/**
 * Контроллер для обработки HTTP‑запросов, связанных с запросами на бронирование предметов.
 * Обеспечивает endpoints для:
 * - создания нового запроса на бронирование;
 * - получения списка запросов текущего пользователя;
 * - просмотра всех запросов (для администраторов/службы поддержки);
 * - детального просмотра конкретного запроса.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    /**
     * Создаёт новый запрос на бронирование предмета.
     */
    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestBody CreateItemRequestDto createItemRequestDto,
            @RequestHeader(CustomHttpHeader.USER_ID) Long requesterId) {
        return itemRequestService.create(createItemRequestDto, requesterId);
    }

    /**
     * Получает список всех запросов на бронирование, созданных текущим пользователем.
     */
    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUser(
            @RequestHeader(CustomHttpHeader.USER_ID) Long requesterId) {
        return itemRequestService.getItemRequestsByUser(requesterId);
    }

    /**
     * Получает полный список всех запросов на бронирование в системе.
     */
    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests() {
        return itemRequestService.getAll();
    }

    /**
     * Получает детальные данные конкретного запроса на бронирование по ID.
     */
    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable("requestId") Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
