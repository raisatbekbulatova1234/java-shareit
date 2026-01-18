package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingPair;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(UpdateItemDto createItemDto, Long userId) {
        User owner = getUser(userId);
        Item item = ItemMapper.toItem(createItemDto, null, owner);
        return ItemMapper.toResponseItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, UpdateItemDto newItemDto, Long userId) {
        Item existingItem = getItem(itemId);
        User user = getUser(userId);

        validateOwner(existingItem, userId);

        Item updatedItem = updateItemFields(existingItem, newItemDto, user);
        return ItemMapper.toResponseItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDto findById(Long itemId) {
        Item item = getItem(itemId);
        List<Comment> comments = getComments(item);
        return ItemMapper.toResponseItemDto(item, comments);
    }

    @Override
    public List<OwnerItemDto> findAllByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(this::mapToOwnerItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> findBySearch(String text) {
        if (isBlank(text)) {
            return List.of();
        }

        return itemRepository.search(text)
                .stream()
                .filter(Item::getAvailable)
                .map(item -> {
                    List<Comment> comments = getComments(item);
                    return ItemMapper.toResponseItemDto(item, comments);
                })
                .toList();
    }

    @Override
    public CommentDto postComment(CreateCommentDto commentDto, Long itemId, Long userId) {
        Item item = getItem(itemId);
        User author = getUser(userId);

        validateUserHasBookedItem(itemId, userId);

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    // === Вспомогательные методы ===


    private void validateOwner(Item item, Long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ConditionsNotMetException(
                    "Пользователь с id " + userId + " не является владельцем вещи с id " + item.getId());
        }
    }

    private Item updateItemFields(Item existing, UpdateItemDto dto, User owner) {
        Item updated = ItemMapper.toItem(dto, existing.getId(), owner);

        updated.setName(coalesce(updated.getName(), existing.getName()));
        updated.setDescription(coalesce(updated.getDescription(), existing.getDescription()));
        updated.setAvailable(coalesce(updated.getAvailable(), existing.getAvailable()));

        return updated;
    }

    private <T> T coalesce(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private OwnerItemDto mapToOwnerItemDto(Item item) {
        BookingPair bookingPair = getLastAndNextBooking(item);
        return ItemMapper.toOwnerItemDto(
                item,
                bookingPair.getLastBooking(),
                bookingPair.getNextBooking()
        );
    }

    private void validateUserHasBookedItem(Long itemId, Long userId) {
        boolean hasPastBooking = bookingRepository.existsPastBookingForUserAndItem(itemId, userId);
        if (!hasPastBooking) {
            throw new ConditionsNotMetException(
                    "Пользователь с id " + userId + " не пользовался вещью с id " + itemId);
        }
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private List<Comment> getComments(Item item) {
        return commentRepository.findAllByItem(item);
    }

    private BookingPair getLastAndNextBooking(Item item) {
        List<Booking> bookings = bookingRepository.findAllByItem(item);

        Booking lastBooking = null;
        Booking nextBooking = null;

        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                nextBooking = booking;
                break; // Первое будущее бронирование — искомое
            }
            lastBooking = booking; // Последнее прошедшее/текущее
        }

        return new BookingPair(lastBooking, nextBooking);
    }
}
