package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Entity
@Table(name = "item_requests", schema = "public")
@Getter
@Setter
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero(message = "Id должен быть больше или равен 0")
    private Long id;

    @Column(name = "description")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    @NotNull(message = "Пользователь не может быть null")
    private User requester;

    @Column(name = "created")
    @NotNull(message = "Дата создания запроса не может быть пустой")
    private LocalDateTime created;
}
