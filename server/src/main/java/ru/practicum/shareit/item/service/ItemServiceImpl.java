package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ResponseItemDto create(RequestItemDto createItemDto, Long userId) {
        User user = getUser(userId);

        Long requestId = createItemDto.getRequestId();
        ItemRequest request = requestId == null
                ? null
                : itemRequestRepository.findById(requestId).orElseThrow(()
                -> new NotFoundException("Item request id not found"));
        Item item = ItemMapper.toItem(createItemDto, null, user, request);

        return ItemMapper.toResponseItemDto(itemRepository.save(item));
    }

    @Override
    public ResponseItemDto update(Long itemId, RequestItemDto newItemDto, Long userId) {
        Item oldItem = getItem(itemId);
        User user = getUser(userId);

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new ConditionsNotMetException("Пользователь с id + " + userId
                    + " не является владельцем вещи с id " + itemId + ".");
        }

        Item newItem = ItemMapper.toItem(newItemDto, itemId, user, oldItem.getRequest());
        if (newItem.getName() == null) {
            newItem.setName(oldItem.getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.getDescription());
        }
        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.getAvailable());
        }
        if (newItem.getRequest() == null) {
            newItem.setRequest(oldItem.getRequest());
        }

        return ItemMapper.toResponseItemDto(itemRepository.save(newItem));
    }

    @Override
    public ResponseItemDto findById(Long itemId) {
        Item item = getItem(itemId);

        return ItemMapper.toResponseItemDto(item, getComments(item));
    }

    @Override
    public List<OwnerItemDto> findAllByOwner(Long ownerId) {
        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(item -> ItemMapper.toOwnerItemDto(item,
                        getLastBooking(item),
                        getNextBooking(item)))
                .toList();
    }

    @Override
    public List<ResponseItemDto> findBySearch(String text) {
        if (text.isEmpty()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(item -> ItemMapper.toResponseItemDto(item, getComments(item)))
                .toList();
    }

    @Override
    public CommentDto postComment(CreateCommentDto commentDto, Long itemId, Long userId) {
        Item item = getItem(itemId);
        User user = getUser(userId);

        if (bookingRepository.findAllByItem(item)
                .stream()
                .filter(book -> book.getEnd().isBefore(LocalDateTime.now()))
                .map(Booking::getBooker)
                .filter(booker -> booker.equals(user))
                .toList().isEmpty()) {
            throw new ConditionsNotMetException("Пользователь с id " + userId + " не пользовался вещью с id " + itemId);
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("Предмет с id " + itemId + " не найден"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private List<Comment> getComments(Item item) {
        return commentRepository.findAllByItem(item);
    }

    private Booking[] getLastAndNextBooking(Item item) {
        final int PAIR_SIZE = 2;
        List<Booking> bookings = bookingRepository.findAllByItem(item);

        Booking[] res = new Booking[PAIR_SIZE];
        res[0] = null;
        res[1] = null;

        for (Booking booking : bookings) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                res[1] = booking;
                break;
            }
            res[0] = booking;
        }

        return res;
    }

    private Booking getLastBooking(Item item) {
        return getLastAndNextBooking(item)[0];
    }

    private Booking getNextBooking(Item item) {
        return getLastAndNextBooking(item)[1];
    }
}
