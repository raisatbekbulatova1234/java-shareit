package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> storage = new ConcurrentHashMap<>();

    @Override
    public List<Item> findByUserId(long userId) {
        return storage.values().stream()
                .filter(item -> item.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item saveItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item не может быть null");
        }

        if (item.getId() == null) {
            // Генерируем новый ID как max(существующих) + 1
            long newId = storage.keySet().stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0L) + 1;
            item.setId(newId);
        }

        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        Item item = storage.get(itemId);

        if (item == null) {
            throw new NoSuchElementException(
                    String.format("Item с ID=%d не найден", itemId)
            );
        }

        if (item.getUserId() != userId) {
            throw new IllegalStateException(
                    String.format(
                            "Item с ID=%d принадлежит другому пользователю (не %d)",
                            itemId, userId
                    )
            );
        }

        storage.remove(itemId);
    }

    @Override
    public Item findById(Long itemId) {
        if (itemId == null || itemId <= 0) {
            return null;
        }
        return storage.get(itemId);
    }

    @Override
    public List<Item> findAllByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return List.of();
        }

        return storage.values().stream()
                .filter(item -> item.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchAvailableByText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        String normalizedText = text.trim().toLowerCase();
        return storage.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(normalizedText) ||
                        item.getDescription().toLowerCase().contains(normalizedText))
                .collect(Collectors.toList());
    }

}
