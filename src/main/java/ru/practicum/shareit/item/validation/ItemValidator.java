package ru.practicum.shareit.item.validation;

import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemValidator {

    void validateUserId(Long userId);

    void validateOwner(Item item, Long userId);

    void validateUpdateDto(ItemUpdateDto updateDto);

    void validateCommentInput(String text, Long itemId, Long userId);
}
