package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@RequestBody UserRequestDto user) {
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable("userId") Long userId,
                       @RequestBody UserRequestDto newUser) {
        return userService.update(userId, newUser);
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteById(@PathVariable("id") Long id) {
        return userService.deleteById(id);
    }
}
