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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        if (containsEmail(user)) {
            throw new DuplicatedDataException("Пользователь с email " + user.getEmail() + " уже существует.");
        }

        return userRepository.save(user);
    }

    @Override
    public User update(Long userId, UserDto newUserDto) {
        User oldUser = getUser(userId);
        User newUser = UserMapper.toUser(newUserDto, userId);

        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        }

        String oldEmail = oldUser.getEmail();

        if (!oldEmail.equals(newUser.getEmail()) && containsEmail(newUser)) {
            throw new DuplicatedDataException("Email " + newUser.getEmail() + " уже занят.");
        }

        return userRepository.save(newUser);
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
        findById(id);

        userRepository.deleteById(id);

        return true;
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private boolean containsEmail(User user) {
        String currentUserEmail = user.getEmail();

        return userRepository.countUsersByEmail(currentUserEmail) != 0;
    }
}
