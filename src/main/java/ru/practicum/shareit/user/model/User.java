package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PositiveOrZero(message = "Id должен быть больше или равен 0")
    private Long id;

    @Column(unique = true, nullable = false)
    @Email(message = "Email указан не по форме")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
