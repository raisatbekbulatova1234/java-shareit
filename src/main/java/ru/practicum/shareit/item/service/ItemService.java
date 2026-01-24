package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(UpdateItemDto item, Long userId);

    ItemDto update(Long itemId, UpdateItemDto itemDto, Long userId);

    ItemDto findById(Long itemId);

    List<OwnerItemDto> findAllByOwner(Long ownerId);

    List<ItemDto> findBySearch(String text);

    CommentDto postComment(CreateCommentDto commentDto, Long itemId, Long userId);
}
