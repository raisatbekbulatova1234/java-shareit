package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    /**
     * Находит все подтверждённые бронирования для конкретного предмета (item).
     * Возвращает список в порядке возрастания времени начала бронирования.
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item = :item " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC")
    List<Booking> findAllByItem(Item item);
}
