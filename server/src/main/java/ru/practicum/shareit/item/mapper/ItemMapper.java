package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoForRequestAnswer;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ResponseItemDto toResponseItemDto(Item item,
                                                    List<Comment> comments,
                                                    Booking lastBooking,
                                                    Booking nextBooking) {
        return new ResponseItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                BookingMapper.toBookingDto(lastBooking),
                BookingMapper.toBookingDto(nextBooking),
                CommentMapper.toCommentDto(comments)
        );
    }

    public static ResponseItemDto toResponseItemDto(Item item, List<Comment> comments) {
        return new ResponseItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                CommentMapper.toCommentDto(comments)
        );
    }

    public static ResponseItemDto toResponseItemDto(Item item) {
        return new ResponseItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static OwnerItemDto toOwnerItemDto(Item item, Booking lastBooking, Booking nextBooking) {
        LocalDateTime lastStart = null;
        LocalDateTime lastEnd = null;

        LocalDateTime nextStart = null;
        LocalDateTime nextEnd = null;

        if (lastBooking != null) {
            lastStart = lastBooking.getStart();
            lastEnd = lastBooking.getEnd();
        }
        if (nextBooking != null) {
            nextStart = nextBooking.getStart();
            nextEnd = nextBooking.getEnd();
        }

        return new OwnerItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastStart,
                lastEnd,
                nextStart,
                nextEnd);
    }

    public static ItemDtoForRequestAnswer toItemDtoForRequestAnswer(Item item) {
        ItemDtoForRequestAnswer res = new ItemDtoForRequestAnswer();

        res.setId(item.getId());
        res.setName(item.getName());
        res.setOwnerId(item.getOwner().getId());

        return res;
    }

    public static Item toItem(RequestItemDto dto, Long id, User user, ItemRequest itemRequest) {
        Item newItem = new Item();

        newItem.setId(id);
        newItem.setName(dto.getName());
        newItem.setDescription(dto.getDescription());
        newItem.setOwner(user);
        newItem.setAvailable(dto.getAvailable());
        newItem.setRequest(itemRequest);

        return newItem;
    }
}
