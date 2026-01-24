package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@Valid @RequestBody UpdateItemDto createItemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {

        return itemService.create(createItemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") @Positive Long itemId,
                          @RequestBody UpdateItemDto itemDto,
                          @RequestHeader(value = "X-Sharer-User-Id") @Positive Long userId) {

        return itemService.update(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable("itemId") @Positive Long itemId) {

        return itemService.findById(itemId);
    }

    @GetMapping
    public List<OwnerItemDto> findAllByOwner(@RequestHeader(value = "X-Sharer-User-Id") @Positive Long ownerId) {

        return itemService.findAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> findBySearch(@RequestParam("text") String text) {
        return itemService.findBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@Valid @RequestBody CreateCommentDto commentDto,
                                  @PathVariable("itemId") Long itemId,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.postComment(commentDto, itemId, userId);
    }
}
