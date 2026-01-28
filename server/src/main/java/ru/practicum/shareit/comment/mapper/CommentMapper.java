package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для преобразования между DTO и моделью комментариев (Comment).
 * Обеспечивает маппинг:
 * - CreateCommentDto → Comment (при создании комментария);
 * - Comment → CommentDto (при возврате данных клиенту);
 * - List<Comment> → List<CommentDto> (при возврате списка комментариев).
 */
public class CommentMapper {

    /**
     * Преобразует DTO создания комментария в сущность Comment.
     */
    public static Comment toComment(CreateCommentDto dto, Item item, User user) {
        Comment comment = new Comment();

        comment.setText(dto.getText());
        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return comment;
    }

    /**
     * Преобразует сущность Comment в DTO для передачи клиенту.
     */
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                ItemMapper.toResponseItemDto(comment.getItem(), null, null, null),
                comment.getUser().getName(),
                comment.getText(),
                comment.getCreated()
        );
    }

    /**
     * Преобразует список сущностей Comment в список DTO для передачи клиенту.
     */
    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
