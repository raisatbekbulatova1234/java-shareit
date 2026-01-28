package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomHttpHeader;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody CreateItemRequestDto createItemRequestDto,
                                            @RequestHeader(CustomHttpHeader.USER_ID) Long requesterId) {
        return itemRequestService.create(createItemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUser(@RequestHeader(CustomHttpHeader.USER_ID) Long requesterId) {
        return itemRequestService.getItemRequestsByUser(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests() {
        return itemRequestService.getAll();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable("requestId") Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }
}
