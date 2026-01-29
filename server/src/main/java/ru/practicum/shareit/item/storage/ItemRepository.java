package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

/**
 * Репозиторий для работы с сущностями Item (предметы) в базе данных.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Находит все предметы, принадлежащие указанному владельцу.
     */
    @Query("SELECT it " +
            "FROM Item it " +
            "JOIN it.owner ow " +
            "WHERE ow.id = ?1")
    List<Item> findAllByOwnerId(Long id);

    /**
     * Осуществляет поиск предметов по текстовой строке в названии или описании.
     * Вне зависимости от регистра.
     */
    @Query("SELECT it FROM Item it " +
            "WHERE UPPER(it.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "   OR UPPER(it.description) LIKE UPPER(CONCAT('%', ?1, '%'))")
    List<Item> search(String text);

    /**
     * Находит все предметы, привязанные к указанному запросу на бронирование.
     */
    List<Item> findAllByRequest(ItemRequest req);
}
