package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User findById(Long userId);

    void delete(Long userId);

    boolean existsById(Long userId);

    boolean existsByEmail(String email);

    User findByEmail(String email);
}
