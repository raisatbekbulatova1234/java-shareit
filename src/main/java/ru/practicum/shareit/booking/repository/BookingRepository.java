package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    //Находит все подтверждённые бронирования предмета, отсортированные по времени начала.

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item = :item " +
            "  AND b.status = 'APPROVED' " +
            "ORDER BY b.start ASC")
    List<Booking> findAllByItem(@Param("item") Item item);

    //Проверяет, есть ли у пользователя завершённое бронирование указанного предмета.

    @Query("SELECT EXISTS (" +
            "   SELECT 1 " +
            "   FROM Booking b " +
            "   WHERE b.item.id = :itemId " +
            "       AND b.booker.id = :userId " +
            "       AND b.end < CURRENT_TIMESTAMP" +
            ")")
    boolean existsPastBookingForUserAndItem(@Param("itemId") Long itemId, @Param("userId") Long userId);
}
