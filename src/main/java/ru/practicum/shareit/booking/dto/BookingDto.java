package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Дата начала должна быть указано")
    private LocalDateTime start;
    @NotNull(message = "Дата окончания должна быть указана")
    private LocalDateTime end;
    @NotNull(message = "Вещь должна быть указана")
    private Long itemId;
    private Long bookerId;
    private BookStatus status;
}

