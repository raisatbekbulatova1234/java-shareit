package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserUpdateRequestDto;

/**
 * Контроллер для обработки HTTP‑запросов, связанных с пользователями (users).
 * Обеспечивает endpoints для:
 * - создания новых пользователей;
 * - обновления существующих пользователей;
 * - получения списка всех пользователей;
 * - получения конкретного пользователя по ID;
 * - удаления пользователя по ID.
 */
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    /**
     * Создаёт нового пользователя.
     */
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequestDto user) {
        log.info("Create user: email={}, name={}", user.getEmail(), user.getName());
        return userClient.create(user);
    }

    /**
     * Обновляет существующего пользователя (частичное обновление).
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(
            @PositiveOrZero @PathVariable("userId") Long userId,
            @Valid @RequestBody UserUpdateRequestDto newUser) {
        log.info("Update user, id = {}", userId);
        return userClient.update(userId, newUser);
    }

    /**
     * Получает список всех пользователей.
     */
    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Find all users");
        return userClient.findAll();
    }

    /**
     * Получает конкретного пользователя по его ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PositiveOrZero @PathVariable("id") Long id) {
        log.info("Find user by id {}", id);
        return userClient.findById(id);
    }

    /**
     * Удаляет пользователя по его ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PositiveOrZero @PathVariable("id") Long id) {
        log.info("Delete user by id {}", id);
        return userClient.deleteById(id);
    }
}
