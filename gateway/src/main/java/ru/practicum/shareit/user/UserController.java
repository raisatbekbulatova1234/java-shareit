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

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequestDto user) {
        log.info("create user");
        return userClient.create(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PositiveOrZero @PathVariable("userId") Long userId,
                                         @Valid @RequestBody UserUpdateRequestDto newUser) {
        log.info("update user, id = {}", userId);
        return userClient.update(userId, newUser);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("find all users");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PositiveOrZero @PathVariable("id") Long id) {
        log.info("find user by id {}", id);
        return userClient.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PositiveOrZero @PathVariable("id") Long id) {
        return userClient.deleteById(id);
    }
}
