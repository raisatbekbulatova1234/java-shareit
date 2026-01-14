package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidator;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserServiceImpl(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
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
        userValidator.validateCreate(userDto); // Валидация при создании


        User user = UserMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("Создан пользователь с ID={}", savedUser.getId());
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = findUserOrThrow(userId);

        userValidator.validateUpdate(userDto, existingUser.getEmail()); // Валидация при обновлении


        // Обновление полей
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
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

    // Вспомогательный метод
    private User findUserOrThrow(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            log.warn("Пользователь с ID={} не найден", userId);
            throw new NoSuchElementException("Пользователь не найден");
        }
        return user;
    }
}
