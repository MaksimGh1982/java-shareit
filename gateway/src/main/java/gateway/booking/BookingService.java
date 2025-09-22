package gateway.booking;

import gateway.BaseClient;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
@Slf4j
@Validated
public class BookingService extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingService(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );

    }

    public ResponseEntity<Object> create(@Valid BookingDto bookingDto, Long userId) {
        log.info("Создать бронирование пользователя  id = " + userId + " вещи id = " + bookingDto.getItemId());
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approved(long bookingId, String approved, long userId) {
        log.info("Подтвердить бронирование id= {} approved= {}", bookingId, approved);
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> findBookingById(long bookingId, long userId) {
        log.info("Показать бронирование id={}", bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingByUser(long userId, BookGetStatus state) {
        log.info("Показать бронирование пользователя id={}", userId);
        return get("?state=" + state.toString(), userId);
    }

    public ResponseEntity<Object> getAllBookingByItemsUser(long userId, BookGetStatus state) {
        log.info("Получение списка бронирований для всех вещей текущего пользователя id={}", userId);
        return get("/owner?state=" + state.toString(), userId);
    }
}
