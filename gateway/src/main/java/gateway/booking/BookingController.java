package gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> create(@RequestBody BookingDto bookingDto, @RequestHeader(SHARER_USER) Long userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approved(@PathVariable long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam String approved) {
        return bookingService.approved(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBooking(@PathVariable long bookingId, @RequestHeader(SHARER_USER) Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingByUser(@RequestHeader(SHARER_USER) Long userId,
                                                      @RequestParam(defaultValue = "ALL") BookGetStatus state) {
        return bookingService.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByItemsUser(@RequestHeader(SHARER_USER) Long userId,
                                                           @RequestParam(defaultValue = "ALL") BookGetStatus state) {
        return bookingService.getAllBookingByItemsUser(userId, state);
    }
}
