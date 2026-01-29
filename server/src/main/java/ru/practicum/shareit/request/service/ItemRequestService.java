package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(CreateItemRequestDto createItemRequestDto, Long requesterId);

    List<ItemRequestDto> getItemRequestsByUser(Long userId);

    List<ItemRequestDto> getAll();

    ItemRequestDto getItemRequestById(Long requestId);
}
