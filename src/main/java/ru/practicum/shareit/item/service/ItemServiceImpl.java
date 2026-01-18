package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    //  ОСНОВНЫЕ ОПЕРАЦИИ С ITEMS

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        itemValidator.validateUserId(userId);
        Item item = ItemMapper.toEntity(itemDto);
        item.setUserId(userId);

        Item savedItem = itemRepository.save(item);
        log.info("Создана вещь. ID: {}, владелец: {}", savedItem.getId(), userId);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemValidator.validateUserId(userId);
        Item item = findItemOrThrow(itemId);

        itemRepository.deleteByUserIdAndItemId(userId, itemId);
        log.info("Удалена вещь. ID: {}, владелец: {}", itemId, userId);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemUpdateDto updateDto, Long userId) {
        itemValidator.validateUserId(userId);
        itemValidator.validateUpdateDto(updateDto);

        Item existingItem = findItemOrThrow(itemId);
        itemValidator.validateOwner(existingItem, userId);

        applyUpdates(existingItem, updateDto);
        Item updatedItem = itemRepository.save(existingItem);

        log.info("Обновлена вещь. ID: {}", updatedItem.getId());
        return ItemMapper.toDto(updatedItem);
    }

    //  ПОЛУЧЕНИЕ ДАННЫХ

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = findItemOrThrow(itemId);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        itemValidator.validateUserId(userId);

        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToItemDtoWithBookingDates)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }

        return itemRepository.search(text.trim())
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public ItemWithCommentsDto getItemWithComments(Long itemId) {
        Item item = findItemOrThrow(itemId);
        ItemWithCommentsDto dto = ItemMapper.toWithCommentsDto(item);
        fillBookingDates(dto, item);
        dto.setComments(getCommentsForItem(itemId));
        return dto;
    }

    @Override
    public List<ItemWithCommentsDto> getItemsByOwnerWithComments(Long userId) {
        itemValidator.validateUserId(userId);

        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(item -> {
                    ItemWithCommentsDto dto = ItemMapper.toWithCommentsDto(item);
                    fillBookingDates(dto, item);
                    dto.setComments(getCommentsForItem(item.getId()));
                    return dto;
                })
                .toList();
    }

    // РАБОТА С КОММЕНТАРИЯМИ

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, String text) {
        itemValidator.validateCommentInput(text, itemId, userId);
        Comment comment = createComment(itemId, userId, text);
        Comment savedComment = commentRepository.save(comment);

        log.info("Добавлен комментарий к вещи ID: {}, пользователь ID: {}", itemId, userId);
        return CommentMapper.toDto(savedComment);
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Вещь с ID=" + itemId + " не найдена"));
    }


    private Comment createComment(Long itemId, Long userId, String text) {
        Comment comment = new Comment();
        comment.setItem(findItemOrThrow(itemId));
        comment.setText(text);
        comment.setAuthor(getUserName(userId));
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"))
                .getName();
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    private ItemDto mapToItemDtoWithBookingDates(Item item) {
        ItemDto dto = ItemMapper.toDto(item);
        fillBookingDates(new ItemWithCommentsDto(), item); // Временный объект для заполнения дат
        return dto;
    }

    private void fillBookingDates(ItemWithCommentsDto dto, Item item) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(
                item.getId(), BookingStatus.APPROVED);

        // Фильтруем и сортируем прошлые бронирования (уже завершились) — по убыванию даты окончания
        List<Booking> pastBookings = bookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .sorted((a, b) -> b.getEnd().compareTo(a.getEnd()))
                .toList();

        // Фильтруем и сортируем будущие бронирования (ещё не начались) — по возрастанию даты начала
        List<Booking> futureBookings = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .sorted((a, b) -> a.getStart().compareTo(b.getStart()))
                .toList();

        // Заполняем lastBooking (самое последнее завершённое бронирование)
        if (!pastBookings.isEmpty()) {
            Booking last = pastBookings.get(0);
            dto.setLastBookingStart(last.getStart());
            dto.setLastBookingEnd(last.getEnd());
        }

        // Заполняем nextBooking (самое ближайшее будущее бронирование)
        if (!futureBookings.isEmpty()) {
            Booking next = futureBookings.get(0);
            dto.setNextBookingStart(next.getStart());
            dto.setNextBookingEnd(next.getEnd());
        }
    }

    //Применяет изменения из ItemUpdateDto к сущности Item.
    // Игнорирует поля со значением null в DTO.

    private void applyUpdates(Item item, ItemUpdateDto updateDto) {
        if (updateDto == null) {
            return;
        }
        if (updateDto.getName() != null) {
            item.setName(updateDto.getName());
        }
        if (updateDto.getDescription() != null) {
            item.setDescription(updateDto.getDescription());
        }
        if (updateDto.getAvailable() != null) {
            item.setAvailable(updateDto.getAvailable());
        }
        if (updateDto.getRequestId() != null) {
            item.setRequestId(updateDto.getRequestId());
        }
    }
}