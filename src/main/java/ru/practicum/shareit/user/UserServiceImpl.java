package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        return UserMapper.toDto(user);
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toDto(savedUser);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId);
        if (existingUser == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        existingUser.setEmail(userDto.getEmail());
        existingUser.setName(userDto.getName());
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDto(updatedUser);
    }

    public void deleteUser(Long userId) {
        if (userRepository.findById(userId) == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        userRepository.delete(userId);
    }
}
