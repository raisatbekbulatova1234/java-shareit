package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.List;

public interface ItemService {
    ResponseItemDto create(RequestItemDto item, Long userId);

    ResponseItemDto update(Long itemId, RequestItemDto itemDto, Long userId);

    ResponseItemDto findById(Long itemId);

    List<OwnerItemDto> findAllByOwner(Long ownerId);

    List<ResponseItemDto> findBySearch(String text);

    CommentDto postComment(CreateCommentDto commentDto, Long itemId, Long userId);
}
