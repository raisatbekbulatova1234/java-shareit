package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = findUserOrThrow(userId);
        return UserMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        userValidator.validateCreate(userDto);

        User user = UserMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("Пользователь создан. ID={}", savedUser.getId());
        return UserMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = findUserOrThrow(userId);

        userValidator.validateUpdate(userDto, existingUser.getEmail());

        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }
        existingUser.setName(userDto.getName());

        User updatedUser = userRepository.save(existingUser);
        log.info("Пользователь обновлен. ID={}", updatedUser.getId());
        return UserMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        findUserOrThrow(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь удален. ID={}", userId);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID={} не найден", userId);
                    return new NoSuchElementException("Пользователь не найден");
                });
    }
}
