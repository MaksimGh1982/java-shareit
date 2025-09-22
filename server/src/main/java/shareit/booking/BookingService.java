package shareit.booking;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import shareit.booking.dto.BookingDto;
import shareit.exception.NotFoundException;
import shareit.exception.ValidException;
import shareit.item.ItemRepository;
import shareit.item.model.Item;
import shareit.user.User;
import shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public Booking create(BookingDto bookingDto, Long userId) {
        log.info("Создать бронирование пользователя  id = " + userId + " вещи id = " + bookingDto.getItemId());
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("пользователь с id = " + userId + " не найден");
        } else {
            bookingDto.setBookerId(userId);
        }
        Item item = itemRepository.findById(bookingDto.getItemId()).orElse(null);
        if (item == null) {
            throw new NotFoundException("вещь с id = " + bookingDto.getItemId() + " не найдена");
        } else if (!item.getAvailable()) {
            throw new ValidException("вещь с id = " + bookingDto.getItemId() + " не доступна");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now()) || bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidException("Неверные даты бронирования start = " + bookingDto.getStart().toString() +
                    " end = " + bookingDto.getEnd().toString());
        }
        bookingDto.setStatus(BookStatus.WAITING);
        return bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));

    }

    public Booking approved(long bookingId, String approved, long userId) {
        log.info("Подтвердить бронирование id= {} approved= {}", bookingId, approved);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new NotFoundException("Бронирование с id = " + bookingId + "не существует");
        }
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ValidException("Подтвердить бронирование может только владелец вещи id = " +
                    booking.getItem().getOwner().getId());
        } else {
            booking.setStatus(approved.equals("true") ? BookStatus.APPROVED : BookStatus.REJECTED);
            return bookingRepository.save(booking);
        }
    }

    public Booking findBookingById(long bookingId, long userId) {
        log.info("Показать бронирование id={}", bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new NotFoundException("Бронирование с id = " + bookingId + "не существует");
        }
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Бронирование с id = " + bookingId + " не принадлежит пользователю id=" + userId);
        } else {
            return booking;
        }
    }

    private BooleanExpression getExprForBookingState(BookGetStatus state) {
        BooleanExpression exprByState = null;
        switch (state) {
            case CURRENT:
                exprByState = QBooking.booking.end.after(LocalDateTime.now()).and(QBooking.booking.start.before(LocalDateTime.now()));
                break;
            case PAST:
                exprByState = QBooking.booking.end.before(LocalDateTime.now());
                break;
            case FUTURE:
                exprByState = QBooking.booking.start.after(LocalDateTime.now());
                break;
            case WAITING:
                exprByState = QBooking.booking.status.eq(BookStatus.WAITING);
                break;
            case REJECTED:
                exprByState = QBooking.booking.status.eq(BookStatus.REJECTED);
                break;
            case ALL:
                exprByState = Expressions.asBoolean(true).isTrue();
                break;
            default:
                throw new NotFoundException("Неверный статус = " + state);
        }
        return exprByState;
    }

    public Collection<Booking> getAllBookingByUser(long userId, BookGetStatus state) {
        log.info("Показать бронирование пользователя id={}", userId);
        if (userRepository.findById(userId).orElse(null) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        BooleanExpression byUserId = QBooking.booking.booker.id.eq(userId);

        return StreamSupport.stream(bookingRepository.findAll(byUserId.and(getExprForBookingState(state)),
                        Sort.by(Sort.Direction.DESC, "start")).spliterator(), false)
                .collect(Collectors.toList());

    }

    public Collection<BookingDto> getAllBookingByItemsUser(long userId, BookGetStatus state) {
        log.info("Получение списка бронирований для всех вещей текущего пользователя id={}", userId);
        if (userRepository.findById(userId).orElse(null) == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        BooleanExpression byUserId = QBooking.booking.item.owner.id.eq(userId);

        return StreamSupport.stream(bookingRepository.findAll(byUserId.and(getExprForBookingState(state)),
                        Sort.by(Sort.Direction.DESC, "start")).spliterator(), false)
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
