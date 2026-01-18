package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero(message = "Id должен быть больше или равен 0")
    private Long id;

    @Column(name = "booking_start")
    private LocalDateTime start;
    @Column(name = "booking_end")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @NotNull(message = "Предмет не может быть null")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    @NotNull(message = "Пользователь не может быть null")
    private User booker;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Статус не может быть null")
    private BookingStatus status;
}
