package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

/**
 * Сервис для работы с запросами на бронирование предметов.
 * Обеспечивает бизнес‑логику: создание, получение и преобразование запросов.
 */
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    /**
     * Создаёт новый запрос на бронирование предмета.
     */
    @Override
    public ItemRequestDto create(CreateItemRequestDto createItemRequestDto, Long requesterId) {
        User requester = getUser(requesterId);
        ItemRequest created = ItemRequestMapper.toItemRequest(createItemRequestDto, requester);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(created));
    }

    /**
     * Получает список всех запросов текущего пользователя.
     */
    @Override
    public List<ItemRequestDto> getItemRequestsByUser(Long userId) {
        User requester = getUser(userId);
        return getItemRequestDtos(itemRequestRepository.findAllByRequester(requester));
    }

    /**
     * Получает полный список всех запросов на бронирование в системе.
     */
    @Override
    public List<ItemRequestDto> getAll() {
        return getItemRequestDtos(itemRequestRepository.findAllOrderByCreated());
    }

    /**
     * Получает детальные данные конкретного запроса по ID.
     * Включает список подходящих предметов, удовлетворяющих запросу.
     */
    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        ItemRequest res = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest с id: " + requestId + " не найден"));
        return ItemRequestMapper.toItemRequestDto(res, itemRepository.findAllByRequest(res));
    }

    /**
     * Преобразует список сущностей ItemRequest в список DTO ItemRequestDto.
     * Для каждого запроса находит подходящие предметы (через itemRepository).
     */
    private List<ItemRequestDto> getItemRequestDtos(List<ItemRequest> itemRequest) {
        return itemRequest.stream()
                .map(req -> ItemRequestMapper.toItemRequestDto(req, itemRepository.findAllByRequest(req)))
                .toList();
    }

    /**
     * Находит пользователя по ID.
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
