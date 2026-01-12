package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(Long userId) {
        super(HttpStatus.NOT_FOUND, String.format("Пользователь с ID=%d не найден", userId));
    }
}
