package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select ir from ItemRequest ir " +
            "where ir.requester = ?1 " +
            "order by ir.created desc")
    List<ItemRequest> findAllByRequester(User requester);

    @Query("select ir from ItemRequest ir order by ir.created desc")
    List<ItemRequest> findAllOrderByCreated();
}
