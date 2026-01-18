package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItemId(Long itemId);

//    // Проверка, арендовал ли пользователь вещь
//    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
//            "WHERE b.item.id = :itemId AND b.booker.id = :userId " +
//            "AND b.end < CURRENT_TIMESTAMP")
//    boolean existsByItemIdAndBookerIdAndStatusAndEndIsBefore(
//            Long itemId, Long userId, BookingStatus status, LocalDateTime end);
}
