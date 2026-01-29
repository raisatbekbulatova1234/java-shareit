package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.util.CustomHttpHeader;

/**
 * Контроллер для обработки HTTP‑запросов, связанных с запросами на предметы (item requests).
 * Обеспечивает endpoints для:
 * - создания новых запросов;
 * - получения списка запросов пользователя;
 * - получения всех запросов (для административных целей);
 * - получения конкретного запроса по ID.
 */
@Controller
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    /**
     * Создаёт новый запрос на предмет.
     */
    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @Valid @RequestBody CreateItemRequestDto createItemRequestDto,
            @PositiveOrZero @RequestHeader(CustomHttpHeader.USER_ID) Long requesterId) {
        log.info("Create item request. Requester ID: {}", requesterId);
        return requestClient.create(createItemRequestDto, requesterId);
    }

    /**
     * Получает все запросы, созданные указанным пользователем.
     */
    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(
            @PositiveOrZero @RequestHeader(CustomHttpHeader.USER_ID) Long requesterId) {
        log.info("Get item requests by user. Requester ID: {}", requesterId);
        return requestClient.getItemRequestsByUser(requesterId);
    }

    /**
     * Получает полный список всех запросов (вероятно, для административных целей).
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        log.info("Get all item requests");
        return requestClient.getAll();
    }

    /**
     * Получает конкретный запрос по его ID.
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(
            @PositiveOrZero @PathVariable("requestId") Long requestId) {
        log.info("Get item request by ID: {}", requestId);
        return requestClient.getItemRequestById(requestId);
    }
}
