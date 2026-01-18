package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OwnerItemDto {
    @PositiveOrZero(message = "Id должен быть больше или равен 0")
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Available не может быть null")
    private Boolean available;
    private LocalDateTime lastStart;
    private LocalDateTime lastEnd;
    private LocalDateTime nextStart;
    private LocalDateTime nextEnd;
}
