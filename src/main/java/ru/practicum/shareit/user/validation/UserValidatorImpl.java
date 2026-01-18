package ru.practicum.shareit.user.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
public class UserValidatorImpl implements UserValidator {

    private final UserRepository userRepository;

    public UserValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validateCreate(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("DTO не может быть null");
        }

        if (userDto.getEmail() == null) {
            throw new IllegalArgumentException("Email не может быть null");
        }

        if (userDto.getName() == null) {
            throw new IllegalArgumentException("Имя не может быть null");
        }

        validateEmailUniqueness(userDto.getEmail());
    }

    @Override
    public void validateUpdate(UserDto userDto, String currentEmail) {
        if (userDto.getEmail() != null) {
            if (!isSameEmailIgnoreCase(userDto.getEmail(), currentEmail)) {
                validateEmailUniqueness(userDto.getEmail());
            }
        }
    }

    @Override
    public void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(
                    "Email '" + email + "' уже используется другим пользователем");
        }
    }

    private boolean isSameEmailIgnoreCase(String email1, String email2) {
        if (email1 == null && email2 == null) return true;
        if (email1 == null || email2 == null) return false;
        return email1.equalsIgnoreCase(email2);
    }
}
