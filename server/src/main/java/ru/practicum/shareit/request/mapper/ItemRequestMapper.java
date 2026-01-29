package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoForRequestAnswer;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        List<ItemDtoForRequestAnswer> answersDto = items == null
                ? List.of()
                : items.stream().map(ItemMapper::toItemDtoForRequestAnswer).toList();

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                answersDto
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return toItemRequestDto(request, null);
    }

    public static ItemRequest toItemRequest(CreateItemRequestDto createItemRequestDto, User requester) {
        ItemRequest req = new ItemRequest();
        req.setDescription(createItemRequestDto.getDescription());
        req.setRequester(requester);
        req.setCreated(LocalDateTime.now());

        return req;
    }
}
