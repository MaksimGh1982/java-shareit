package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking create(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking approved(@PathVariable long bookingId,
                            @RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestParam String approved) {
        return bookingService.approved(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public Booking findBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public Collection<Booking> GetAllBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "ALL") BookGetStatus state) {
        return bookingService.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> GetAllBookingByItemsUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @RequestParam(defaultValue = "ALL") BookGetStatus state) {
        return bookingService.getAllBookingByItemsUser(userId, state);
    }
}
