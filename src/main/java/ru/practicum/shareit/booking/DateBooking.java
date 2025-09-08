package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

public record DateBooking(
        LocalDateTime start,
        LocalDateTime end) {
}
