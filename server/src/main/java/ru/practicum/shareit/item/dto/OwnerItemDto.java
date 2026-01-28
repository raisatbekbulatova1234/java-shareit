package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OwnerItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastStart;
    private LocalDateTime lastEnd;
    private LocalDateTime nextStart;
    private LocalDateTime nextEnd;
}
