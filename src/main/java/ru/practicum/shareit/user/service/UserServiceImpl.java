package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        validateUserForCreation(user);
        return userRepository.save(user);
    }

    @Override
    public User update(Long userId, UserDto newUserDto) {
        User existingUser = getUser(userId);
        User updatedUser = UserMapper.toUser(newUserDto, userId);

        applyUpdates(existingUser, updatedUser);
        validateEmailUniqueness(updatedUser, existingUser.getEmail());


        return userRepository.save(updatedUser);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return getUser(id);
    }

    @Override
    public boolean deleteById(Long id) {
        getUser(id); // Проверяем существование
        userRepository.deleteById(id);
        return true;
    }

    // === Вспомогательные методы ===

    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        if (isBlank(user.getEmail())) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        if (emailExists(user.getEmail())) {
            throw new DuplicatedDataException(
                    "Пользователь с email " + user.getEmail() + " уже существует");
        }
    }

    private void applyUpdates(User existing, User updated) {
        updated.setName(coalesce(updated.getName(), existing.getName()));
        updated.setEmail(coalesce(updated.getEmail(), existing.getEmail()));
    }

    private void validateEmailUniqueness(User updated, String oldEmail) {
        String newEmail = updated.getEmail();
        if (!Objects.equals(oldEmail, newEmail) && emailExists(newEmail)) {
            throw new DuplicatedDataException("Email " + newEmail + " уже занят");
        }
    }

    private boolean emailExists(String email) {
        return userRepository.countUsersByEmail(email) > 0;
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь с id " + id + " не найден"));
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private <T> T coalesce(T value, T defaultValue) {
        return (value != null) ? value : defaultValue;
    }
}
