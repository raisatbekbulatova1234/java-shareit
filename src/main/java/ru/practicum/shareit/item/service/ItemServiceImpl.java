package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> getItems(long userId) {
        return itemRepository.findByUserId(userId);
    }


    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        if (!userExists(userId)) {
            throw new UserNotFoundException(userId);
        }

        Item item = ItemMapper.toEntity(itemDto);
        item.setUserId(userId);
        Item savedItem = itemRepository.saveItem(item);
        return ItemMapper.toDto(savedItem);
    }

    private boolean userExists(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        return userRepository.existsById(userId);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        if (userId <= 0 || itemId <= 0) {
            throw new IllegalArgumentException("userId и itemId должны быть положительными числами");
        }
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto updateDto, Long userId) {
        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null) {
            throw new NoSuchElementException("Вещь не найдена");
        }
        if (!existingItem.getUserId().equals(userId)) {
            throw new ForbiddenException("Только владелец может редактировать вещь");
        }

        // Обновляем только не‑null поля
        if (updateDto.getName() != null) {
            existingItem.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            existingItem.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAvailable() != null) {
            existingItem.setAvailable(updateDto.getAvailable());
        }
        if (updateDto.getRequestId() != null) {
            existingItem.setRequestId(updateDto.getRequestId());
        }

        Item updatedItem = itemRepository.saveItem(existingItem);
        return ItemMapper.toDto(updatedItem);
    }


    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NoSuchElementException("Вещь с ID=" + itemId + " не найдена");
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID пользователя должен быть положительным числом");
        }
        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .toList(); // проще, чем Collectors.toList()
    }


    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        String normalizedText = text.trim().toLowerCase();

        return itemRepository.searchAvailableByText(normalizedText).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

}
