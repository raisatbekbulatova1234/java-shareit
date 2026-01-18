package ru.practicum.shareit.item.comment;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthor(comment.getAuthor());
        dto.setCreated(comment.getCreated());
        return dto;
    }
}

