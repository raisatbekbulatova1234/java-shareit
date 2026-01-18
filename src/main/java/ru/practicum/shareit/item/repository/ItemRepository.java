package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it " +
            "from Item it " +
            "join it.owner ow " +
            "where ow.id = ?1")
    List<Item> findAllByOwnerId(Long id);

    @Query("select it from Item it " +
            "where upper(it.name) like upper(concat('%', ?1, '%'))" +
            "or upper(it.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text);
}
