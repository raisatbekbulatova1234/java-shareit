package ru.practicum.shareit.user.validation;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserValidator {

    void validateCreate(UserDto userDto);

    void validateUpdate(UserDto userDto, String currentEmail);

    void validateEmailUniqueness(String email);
}
