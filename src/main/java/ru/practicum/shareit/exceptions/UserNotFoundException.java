package ru.practicum.shareit.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super(String.format("Пользователь с ID=%d не найден", userId));
    }
}

