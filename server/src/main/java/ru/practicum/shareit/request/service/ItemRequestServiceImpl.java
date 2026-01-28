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

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(CreateItemRequestDto createItemRequestDto, Long requesterId) {
        User requester = getUser(requesterId);
        ItemRequest created = ItemRequestMapper.toItemRequest(createItemRequestDto, requester);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(created));
    }

    @Override
    public List<ItemRequestDto> getItemRequestsByUser(Long userId) {
        User requester = getUser(userId);

        return getItemRequestDtos(itemRequestRepository.findAllByRequester(requester));
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return getItemRequestDtos(itemRequestRepository.findAllOrderByCreated());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        ItemRequest res = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest с id: " + requestId + " не найден"));

        return ItemRequestMapper.toItemRequestDto(res, itemRepository.findAllByRequest(res));
    }

    private List<ItemRequestDto> getItemRequestDtos(List<ItemRequest> itemRequest) {
        return itemRequest.stream()
                .map(req
                        -> ItemRequestMapper.toItemRequestDto(req, itemRepository.findAllByRequest(req)))
                .toList();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
