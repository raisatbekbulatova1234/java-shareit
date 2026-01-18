package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, Long userId);

    void deleteItem(long userId, long itemId);

    ItemDto updateItem(Long itemId, ItemUpdateDto updateDto, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByOwner(Long userId);

    List<ItemDto> searchItems(String text);
}
