package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название вещи (name) не может быть пустым или содержать только пробелы")
    private String name;

    @NotBlank(message = "Описание вещи (description) не может быть пустым или содержать только пробелы")
    private String description;

    @NotNull(message = "Статус доступности (available) не может быть null")
    private Boolean available;

    @NotNull(message = "ID владельца (userId) не может быть null")
    private Long userId; // владелец
    private Long requestId; // ID запроса, по которому создана вещь (может быть null)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
