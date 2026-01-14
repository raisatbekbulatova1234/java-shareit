package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = findUserOrThrow(userId);
        return UserMapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // Валидация обязательных полей
        validateRequiredFields(userDto);

        // Проверка уникальности email
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException(
                    "Пользователь с email '" + userDto.getEmail() + "' уже существует");
        }

        User user = UserMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("Создан пользователь с ID={}", savedUser.getId());
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = findUserOrThrow(userId);

        // Обновление email (если указан)
        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            if (!isSameEmailIgnoreCase(userDto.getEmail(), existingUser.getEmail())) {
                if (userRepository.existsByEmail(userDto.getEmail())) {
                    throw new ConflictException(
                            "Email '" + userDto.getEmail() + "' уже используется другим пользователем");
                }
                existingUser.setEmail(userDto.getEmail());
            }
        }

        existingUser.setName(userDto.getName());

        User updatedUser = userRepository.save(existingUser);
        log.info("Обновлён пользователь с ID={}", updatedUser.getId());
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        findUserOrThrow(userId); // Проверяем существование
        userRepository.delete(userId);
        log.info("Удален пользователь с ID={}", userId);
    }

    // Вспомогательные методы
    private User findUserOrThrow(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            log.warn("Пользователь с ID={} не найден", userId);
            throw new NoSuchElementException("Пользователь не найден");
        }
        return user;
    }

    private void validateRequiredFields(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("DTO не может быть null");
        }

        if (userDto.getEmail() == null) {
            throw new IllegalArgumentException("Email не может быть null");
        }

        if (userDto.getName() == null) {
            throw new IllegalArgumentException("Имя не может быть null");
        }
    }

    private void validateEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email не может быть null");
        }
    }

    private boolean isSameEmailIgnoreCase(String email1, String email2) {
        if (email1 == null && email2 == null) return true;
        if (email1 == null || email2 == null) return false;
        return email1.equalsIgnoreCase(email2);
    }
}
