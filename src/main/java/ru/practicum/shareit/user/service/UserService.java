package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User update(Long userId, UserDto newUserDto);

    List<User> findAll();

    User findById(Long id);

    boolean deleteById(Long id);
}
