package ru.practicum.shareit.Item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Item.dto.CreateCommentDto;
import ru.practicum.shareit.Item.dto.RequestItemDto;
import ru.practicum.shareit.util.CustomHttpHeader;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody RequestItemDto createItemDto,
                                             @PositiveOrZero @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        log.info("Create Item userId = {}", userId);
        return itemClient.createItem(createItemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PositiveOrZero @PathVariable("itemId") Long itemId,
                                         @Valid @RequestBody RequestItemDto itemDto,
                                         @PositiveOrZero @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        log.info("Update Item itemId = {}, userId = {}", itemId, userId);
        return itemClient.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PositiveOrZero @PathVariable("itemId") Long itemId) {
        log.info("Get item by id = {}", itemId);
        return itemClient.findById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@PositiveOrZero @RequestHeader(CustomHttpHeader.USER_ID) Long ownerId) {
        log.info("Get all items by owner = {}", ownerId);
        return itemClient.findAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findBySearch(@RequestParam("text") String text) {
        log.info("Get all items by search = {}", text);
        return itemClient.findBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@Valid @RequestBody CreateCommentDto commentDto,
                                              @PositiveOrZero @PathVariable("itemId") Long itemId,
                                              @PositiveOrZero @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        log.info("Post comment itemId = {}, userId = {}", itemId, userId);
        return itemClient.postComment(commentDto, itemId, userId);
    }
}
