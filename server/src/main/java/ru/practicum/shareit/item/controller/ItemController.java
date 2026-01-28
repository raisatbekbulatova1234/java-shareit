package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.CustomHttpHeader;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseItemDto create(@RequestBody RequestItemDto createItemDto,
                                  @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        return itemService.create(createItemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseItemDto update(@PathVariable("itemId") Long itemId,
                                  @RequestBody RequestItemDto itemDto,
                                  @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        return itemService.update(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto findById(@PathVariable("itemId") Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<OwnerItemDto> findAllByOwner(@RequestHeader(CustomHttpHeader.USER_ID) Long ownerId) {
        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ResponseItemDto> findBySearch(@RequestParam("text") String text) {
        return itemService.findBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestBody CreateCommentDto commentDto,
                                  @PathVariable("itemId") Long itemId,
                                  @RequestHeader(CustomHttpHeader.USER_ID) Long userId) {
        return itemService.postComment(commentDto, itemId, userId);
    }
}
