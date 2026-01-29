package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    List<User> findAll();

    Optional<User> findById(Long id);

    User update(User newUser);

    boolean delete(Long id);
}
