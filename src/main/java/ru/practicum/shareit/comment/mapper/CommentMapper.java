package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class CommentMapper {
    public static Comment toComment(CreateCommentDto dto, Item item, User user) {
        Comment comment = new Comment();

        comment.setText(dto.getText());
        comment.setUser(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                ItemMapper.toResponseItemDto(comment.getItem(), null, null, null),
                comment.getUser().getName(),
                comment.getText(),
                comment.getCreated()
        );
    }

    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments != null ? comments.stream().map(CommentMapper::toCommentDto).toList() : null;
    }
}
