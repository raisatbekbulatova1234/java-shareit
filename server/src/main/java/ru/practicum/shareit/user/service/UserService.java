package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User create(UserRequestDto user);

    User update(Long userId, UserRequestDto newUserDto);

    List<User> findAll();

    User findById(Long id);

    boolean deleteById(Long id);
}
