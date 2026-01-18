package ru.practicum.shareit.item.validation;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class ItemValidatorImpl implements ItemValidator {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public ItemValidatorImpl(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void validateUserId(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь", userId);
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

    public void validateCommentInput(String text, Long itemId, Long userId) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }

        boolean hasBooked = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasBooked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Вы не можете оставить комментарий: вы не арендовали эту вещь");
        }
    }
}
