package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CommentMapperTest {
    @Test
    public void nullCommentListShouldReturnNull() {
        List<Comment> nullList = null;

        assertThat(CommentMapper.toCommentDto(nullList), nullValue());
    }
}
