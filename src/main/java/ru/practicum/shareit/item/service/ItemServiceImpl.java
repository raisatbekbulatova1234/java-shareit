package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidator;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    public ItemServiceImpl(ItemRepository itemRepository, ItemValidator itemValidator) {
        this.itemRepository = itemRepository;
        this.itemValidator = itemValidator;
    }

    @Override
    public List<Item> getItems(long userId) {
        return itemRepository.findByUserId(userId);
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        itemValidator.validateUserId(userId);
        Item item = ItemMapper.toEntity(itemDto);
        item.setUserId(userId);
        Item savedItem = itemRepository.saveItem(item);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        itemValidator.validateUserId(userId);
        itemValidator.validateItemId(itemId);
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemUpdateDto updateDto, Long userId) {
        itemValidator.validateItemId(itemId);
        itemValidator.validateUserId(userId);
        itemValidator.validateUpdateDto(updateDto);

        Item existingItem = itemRepository.findById(itemId);
        if (existingItem == null) {
            throw new NoSuchElementException("Вещь с ID=" + itemId + " не найдена");
        }

        itemValidator.validateOwner(existingItem, userId);

        applyUpdates(existingItem, updateDto);
        Item updatedItem = itemRepository.saveItem(existingItem);
        return ItemMapper.toDto(updatedItem);
    }


    @Override
    public ItemDto getItemById(Long itemId) {
        itemValidator.validateItemId(itemId);
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new NoSuchElementException("Вещь с ID=" + itemId + " не найдена");
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        itemValidator.validateUserId(userId); // Используем валидатор вместо ручной проверки
        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        String normalizedText = text.trim().toLowerCase();
        return itemRepository.searchAvailableByText(normalizedText)
                .stream()
                .map(ItemMapper::toDto)
                .toList(); // Используем toList() вместо Collectors.toList()
    }

    private void applyUpdates(Item item, ItemUpdateDto updateDto) {
        if (updateDto.getName() != null) item.setName(updateDto.getName());
        if (updateDto.getDescription() != null) item.setDescription(updateDto.getDescription());
        if (updateDto.getAvailable() != null) item.setAvailable(updateDto.getAvailable());
        if (updateDto.getRequestId() != null) item.setRequestId(updateDto.getRequestId());
    }
}
