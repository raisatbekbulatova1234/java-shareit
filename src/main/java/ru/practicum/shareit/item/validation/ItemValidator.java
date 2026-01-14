package ru.practicum.shareit.item.validation;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemValidator {

    void validateUserId(Long userId);

    void validateItemId(Long itemId);


    void validateOwner(Item item, Long userId);

    void validateUpdateDto(ItemUpdateDto updateDto);
}
