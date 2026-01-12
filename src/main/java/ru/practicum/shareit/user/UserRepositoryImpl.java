package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> storage = new ConcurrentHashMap<>();
    private long nextId = 1L;

    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    public User findById(Long id) {
        return storage.get(id);
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
        }
        storage.put(user.getId(), user);
        return user;
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long userId) {
        // Проверяем, что userId не null и есть в хранилище
        if (userId == null) {
            return false;
        }
        return storage.containsKey(userId);
    }
}
