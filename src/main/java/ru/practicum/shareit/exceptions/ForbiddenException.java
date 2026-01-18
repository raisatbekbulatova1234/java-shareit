package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeException {

    private final HttpStatus status;

    public ForbiddenException(String message) {
        super(message);
        this.status = HttpStatus.FORBIDDEN; // Статус по умолчанию
    }

    public ForbiddenException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}

