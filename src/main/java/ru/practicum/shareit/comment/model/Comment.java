package ru.practicum.shareit.comment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "userId не может быть пустым")
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @NotNull(message = "itemId не может быть пустым")
    private Item item;

    @Column(name = "comment")
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;

    @Column(name = "created")
    private LocalDateTime created;
}
