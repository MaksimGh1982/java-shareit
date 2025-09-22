import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shareit.ShareItApp;
import shareit.booking.*;
import shareit.booking.dto.BookingDto;
import shareit.item.ItemService;
import shareit.item.dto.ItemDto;
import shareit.user.UserService;
import shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "application-test.properties",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShareItApp.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;


    @Test
    void testSaveBooking() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(ItemTest.makeItemDto("Computer", "apple", true), retUserDto.getId());

        BookingDto bookingDto = makeBookingDtoDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                retItemDto.getId());

        Booking retBooking = bookingService.create(bookingDto, retUserDto.getId());

        TypedQuery<Booking> query = em.createQuery("Select u from Booking u where u.id = :id", Booking.class);
        Booking checkBooking = query.setParameter("id", retBooking.getId())
                .getSingleResult();

        assertThat(checkBooking.getId(), notNullValue());
        assertThat(checkBooking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(checkBooking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(checkBooking.getBooker().getId(), equalTo(retUserDto.getId()));
        assertThat(checkBooking.getItem().getId(), equalTo(retItemDto.getId()));
    }

    @Test
    void testBookingApproved() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(ItemTest.makeItemDto("Computer", "apple", true), retUserDto.getId());

        BookingDto bookingDto = makeBookingDtoDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                retItemDto.getId());

        Booking retBooking = bookingService.create(bookingDto, retUserDto.getId());

        bookingService.approved(retBooking.getId(), "true", retUserDto.getId());

        TypedQuery<Booking> query = em.createQuery("Select u from Booking u where u.id = :id", Booking.class);
        Booking checkBooking = query.setParameter("id", retBooking.getId())
                .getSingleResult();

        assertThat(checkBooking.getId(), notNullValue());
        assertThat(checkBooking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(checkBooking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(checkBooking.getBooker().getId(), equalTo(retUserDto.getId()));
        assertThat(checkBooking.getStatus(), equalTo(BookStatus.APPROVED));
        assertThat(checkBooking.getItem().getId(), equalTo(retItemDto.getId()));
    }

    @Test
    void testFindBooking() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(ItemTest.makeItemDto("Computer", "apple", true), retUserDto.getId());

        BookingDto bookingDto = makeBookingDtoDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                retItemDto.getId());

        Booking retBooking = bookingService.create(bookingDto, retUserDto.getId());

        Booking checkBooking = bookingService.findBookingById(retBooking.getId(), retUserDto.getId());

        assertThat(checkBooking.getId(), notNullValue());
        assertThat(checkBooking.getStart(), equalTo(bookingDto.getStart()));
        assertThat(checkBooking.getEnd(), equalTo(bookingDto.getEnd()));
        assertThat(checkBooking.getBooker().getId(), equalTo(retUserDto.getId()));
        assertThat(checkBooking.getStatus(), equalTo(BookStatus.WAITING));
        assertThat(checkBooking.getItem().getId(), equalTo(retItemDto.getId()));
    }

   private BookingDto makeBookingDtoDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingDto dto = new BookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);

        return dto;
    }
}

