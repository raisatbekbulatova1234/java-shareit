package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User findById(Long userId);

    void delete(Long userId);
}
