package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

/**
 * Репозиторий для работы с сущностями ItemRequest (запросы на бронирование предметов) в базе данных.
 */
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    /**
     * Находит все запросы на бронирование, созданные указанным пользователем (заявителем).
     * Результаты сортируются по дате создания в порядке убывания (новые — первыми).
     */
    @Query("SELECT ir FROM ItemRequest ir " +
            "WHERE ir.requester = ?1 " +
            "ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequester(User requester);

    /**
     * Получает полный список всех запросов на бронирование в системе.
     * Результаты сортируются по дате создания в порядке убывания (новые — первыми).
     */
    @Query("SELECT ir FROM ItemRequest ir ORDER BY ir.created DESC")
    List<ItemRequest> findAllOrderByCreated();
}
