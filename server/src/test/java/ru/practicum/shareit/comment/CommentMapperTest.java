package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Тест-класс для проверки маппера CommentMapper.
 * Проверяет корректность преобразования сущностей Comment в DTO (и обратно, если реализовано).
 */
public class CommentMapperTest {

    @Test
    public void nullCommentListShouldReturnNull() {
        // Исходные данные: null-список комментариев
        List<Comment> nullList = null;

        // Вызов метода маппера для преобразования null-списка
        var result = CommentMapper.toCommentDto(nullList);

        // Проверка: результат должен быть null
        assertThat(result, nullValue());
    }
}
