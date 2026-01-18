package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    void deleteItem(long userId, long itemId);

    ItemDto updateItem(Long itemId, ItemUpdateDto updateDto, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);
    ItemWithCommentsDto getItemWithComments(Long itemId);

    List<ItemWithCommentsDto> getItemsByOwnerWithComments(Long userId);

    CommentDto addComment(Long itemId, Long userId, String text);
}
