package ru.practicum.shareit.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String text, Long userId) {
        super(String.format("Не найдено! %s с ID=%d",text, userId));
    }
}


