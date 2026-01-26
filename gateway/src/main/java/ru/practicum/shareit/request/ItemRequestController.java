package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.util.CustomHttpHeader;

@Controller
@RequestMapping("/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody CreateItemRequestDto createItemRequestDto,
                                                    @PositiveOrZero
                                                    @RequestHeader(CustomHttpHeader.USER_ID) Long requesterId) {
        return requestClient.create(createItemRequestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUser(@PositiveOrZero
                                                        @RequestHeader(CustomHttpHeader.USER_ID)
                                                        Long requesterId) {
        return requestClient.getItemRequestsByUser(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        return requestClient.getAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PositiveOrZero @PathVariable("requestId") Long requestId) {
        return requestClient.getItemRequestById(requestId);
    }
}
