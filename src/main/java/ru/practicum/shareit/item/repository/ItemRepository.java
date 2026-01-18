package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    //Находит все предметы, принадлежащие пользователю с указанным ID.

    @Query("SELECT it " +
            "FROM Item it " +
            "JOIN it.owner ow " +
            "WHERE ow.id = :ownerId")
    List<Item> findAllByOwnerId(@Param("ownerId") Long ownerId);

    //Ищет предметы по ключевому слову в названии или описании (без учёта регистра).

    @Query("SELECT it " +
            "FROM Item it " +
            "WHERE UPPER(it.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "   OR UPPER(it.description) LIKE UPPER(CONCAT('%', :text, '%'))")
    List<Item> search(@Param("text") String text);
}
