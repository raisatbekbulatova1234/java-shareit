package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> findByUserId(long userId);

    Item saveItem(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);

    Item findById(Long itemId);

    List<Item> findAllByUserId(Long userId);

    List<Item> searchAvailableByText(String text);

}

