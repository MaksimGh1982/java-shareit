package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Наимнование должно быть указано")
    private String name;
    @NotBlank(message = "Описание должно быть указано")
    private String description;
    @NotNull(message = "Доступность должно быть указано")
    private Boolean available;
    private Long owner;
    private Long request;
}
