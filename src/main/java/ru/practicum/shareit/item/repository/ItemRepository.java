package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    Optional<Item> findById(Long id);

    List<Item> findAllByUserId(Long userId);

    @Query("DELETE FROM Item i WHERE i.userId = :userId AND i.id = :itemId")
    void deleteByUserIdAndItemId(Long userId, Long itemId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.available = TRUE " +
            "  AND (UPPER(i.name) LIKE CONCAT('%', UPPER(:text), '%') " +
            "       OR UPPER(i.description) LIKE CONCAT('%', UPPER(:text), '%'))")
    List<Item> search(String text);

    boolean existsById(Long id);
}
