package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemWithCommentsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    // Даты бронирований
    private LocalDateTime lastBookingStart;
    private LocalDateTime lastBookingEnd;
    private LocalDateTime nextBookingStart;
    private LocalDateTime nextBookingEnd;

    // Список комментариев
    private List<CommentDto> comments;
}
