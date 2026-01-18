package ru.practicum.shareit.item.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Objects;

@Component
public class ItemValidatorImpl implements ItemValidator {

    private final UserRepository userRepository;

    public ItemValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validateUserId(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    @Override
    public void validateOwner(Item item, Long userId) {
        if (!Objects.equals(item.getUserId(), userId)) {
            throw new ForbiddenException("Только владелец может редактировать вещь");
        }
    }

    @Override
    public void validateUpdateDto(ItemUpdateDto updateDto) {
        if (updateDto.getName() == null &&
                updateDto.getDescription() == null &&
                updateDto.getAvailable() == null &&
                updateDto.getRequestId() == null) {
            throw new IllegalArgumentException("Необходимо указать хотя бы одно поле для обновления");
        }
    }
}
