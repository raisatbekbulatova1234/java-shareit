package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;
    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemValidator itemValidator, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.itemValidator = itemValidator;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        itemValidator.validateUserId(userId);
        Item item = ItemMapper.toEntity(itemDto);
        item.setUserId(userId);
        Item savedItem = itemRepository.save(item);
        log.info("Вещь создана. ID={}, владелец={}", savedItem.getId(), userId);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemValidator.validateUserId(userId);
        findItemOrThrow(itemId); // Проверяем существование
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
        log.info("Вещь удалена. ID={}, владелец={}", itemId, userId);
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
        log.info("Вещь обновлена. ID={}", updatedItem.getId());
        return ItemMapper.toDto(updatedItem);
    }

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
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Вещь с ID=" + itemId + " не найдена"));
    }

    // Вспомогательный метод: заполняет last/next booking в DTO
    private void fillBookingDates(ItemDto dto, Item item) {
        LocalDateTime now = LocalDateTime.now();

        // Получаем все бронирования вещи в статусе APPROVED
        List<Booking> bookings = bookingRepository.findAllByItemIdAndStatus(
                item.getId(), BookingStatus.APPROVED);

        // Фильтруем и сортируем
        List<Booking> pastBookings = bookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .sorted((a, b) -> b.getEnd().compareTo(a.getEnd())) // по убыванию end
                .toList();

        List<Booking> futureBookings = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .sorted((a, b) -> a.getStart().compareTo(b.getStart())) // по возрастанию start
                .toList();

        // Заполняем lastBooking
        if (!pastBookings.isEmpty()) {
            Booking last = pastBookings.get(0);
            dto.setLastBookingStart(last.getStart());
            dto.setLastBookingEnd(last.getEnd());
        }

        // Заполняем nextBooking
        if (!futureBookings.isEmpty()) {
            Booking next = futureBookings.get(0);
            dto.setNextBookingStart(next.getStart());
            dto.setNextBookingEnd(next.getEnd());
        }
    }

    private void applyUpdates(Item item, ItemUpdateDto updateDto) {
        if (updateDto == null) {
            return;
        }
        if (updateDto.getName() != null) item.setName(updateDto.getName());
        if (updateDto.getDescription() != null) item.setDescription(updateDto.getDescription());
        if (updateDto.getAvailable() != null) item.setAvailable(updateDto.getAvailable());
        if (updateDto.getRequestId() != null) item.setRequestId(updateDto.getRequestId());
    }
}
