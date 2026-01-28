package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

/**
 * Сервис для работы с пользователями в системе.
 * Обеспечивает бизнес‑логику: создание, обновление, поиск и удаление пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /**
     * Создаёт нового пользователя.
     * @Transactional гарантирует атомарность операции: если проверка на дубликат
     * или сохранение провалятся — транзакция будет откатана.
     */
    @Override
    @Transactional
    public User create(UserRequestDto userDto) {
        User user = UserMapper.toUser(userDto);

        if (containsEmail(user)) {
            throw new DuplicatedDataException(
                    "Пользователь с email " + user.getEmail() + " уже существует."
            );
        }
        return userRepository.save(user);
    }

    /**
     * Обновляет данные существующего пользователя.
     * @Transactional обеспечивает целостность: если проверка email или сохранение
     * завершится ошибкой — изменения не применятся.
     */
    @Override
    @Transactional
    public User update(Long userId, UserRequestDto newUserDto) {
        User oldUser = getUser(userId);
        User newUser = UserMapper.toUser(newUserDto);
        newUser.setId(userId);

        // Сохраняем старые значения, если новые не указаны
        if (newUser.getName() == null) newUser.setName(oldUser.getName());
        if (newUser.getEmail() == null) newUser.setEmail(oldUser.getEmail());

        String oldEmail = oldUser.getEmail();

        // Проверяем, изменился ли email и не занят ли он
        if (!oldEmail.equals(newUser.getEmail()) && containsEmail(newUser)) {
            throw new DuplicatedDataException("Email " + newUser.getEmail() + " уже занят.");
        }

        return userRepository.save(newUser);
    }

    /**
     * Получает список всех пользователей в системе.
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Находит пользователя по ID.
     */
    @Override
    public User findById(Long id) {
        return getUser(id);
    }

    /**
     * Удаляет пользователя по ID.
     */
    @Override
    @Transactional
    public boolean deleteById(Long id) {
        findById(id); // Проверяем существование пользователя
        userRepository.deleteById(id);
        return true;
    }

    /**
     * Вспомогательный метод для поиска пользователя по ID.
     */
    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    /**
     * Проверяет, существует ли пользователь с указанным email в системе.
     */
    private boolean containsEmail(User user) {
        String currentUserEmail = user.getEmail();
        return userRepository.countUsersByEmail(currentUserEmail) != 0;
    }
}
