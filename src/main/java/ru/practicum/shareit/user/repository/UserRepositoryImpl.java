package ru.practicum.shareit.user.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private long nextId = 1L;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public User findById(Long id) {
        User user = storage.get(id);
        if (user == null) {
            log.debug("Пользователь с ID={} не найден", id);
        }
        return user;  // Возвращает null, если ID нет
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
            log.debug("Создан новый пользователь с ID={}", user.getId());
        } else {
            log.debug("Обновляется пользователь с ID={}", user.getId());
        }
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        if (storage.remove(id) != null) {
            log.info("Пользователь с ID={} удалён", id);
        } else {
            log.warn("Попытка удаления пользователя с несуществующим ID={}", id);
        }
    }

    @Override
    public boolean existsById(Long userId) {
        boolean exists = userId != null && storage.containsKey(userId);
        if (!exists && userId != null) {
            log.debug("Пользователь с ID={} не существует", userId);
        }
        return exists;
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;  // null не считается существующим email
        }
        boolean exists = storage.values().stream()
                .anyMatch(user -> user.getEmail() != null
                        && user.getEmail().equalsIgnoreCase(email));
        if (exists) {
            log.debug("Email '{}' уже существует в системе", email);
        }
        return exists;
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            log.debug("Поиск по email=null — результат: не найден");
            return null;
        }

        return storage.values().stream()
                .filter(user -> user.getEmail() != null
                        && user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
}
