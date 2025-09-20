import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shareit.ShareItApp;
import shareit.booking.Booking;
import shareit.booking.BookingService;
import shareit.booking.dto.BookingDto;
import shareit.item.ItemService;
import shareit.item.dto.ItemDto;
import shareit.item.model.Item;
import shareit.request.ItemRequest;
import shareit.request.RequestService;
import shareit.request.dto.ItemRequestDto;
import shareit.user.User;
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
public class intgrTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final RequestService requestService;
    private final BookingService bookingService;

    @Test
    void testSaveUser() {

        UserDto retUserDto = userService.create(makeUserDto("Ivanov Ivan", "some@email.com"));

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", retUserDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo("Ivanov Ivan"));
        assertThat(user.getEmail(), equalTo("some@email.com"));

    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }

    @Test
    void testSaveItem() {

        UserDto retUserDto = userService.create(makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(makeItemDto("Computer", "apple", false), retUserDto.getId());

        TypedQuery<Item> query = em.createQuery("Select u from Item u where u.id = :id", Item.class);
        Item checkItem = query.setParameter("id", retItemDto.getId())
                .getSingleResult();

        assertThat(checkItem.getId(), notNullValue());
        assertThat(checkItem.getName(), equalTo("Computer"));
        assertThat(checkItem.getDescription(), equalTo("apple"));
        assertThat(checkItem.getAvailable(), equalTo(false));
        assertThat(checkItem.getOwner().getId(), equalTo(retItemDto.getOwner()));

    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);

        return dto;
    }

    @Test
    void testSaveRequest() {

        UserDto retUserDto = userService.create(makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemRequestDto retItemRequestDto = requestService.create(makeItemRequestDto("Computer"), retUserDto.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select u from ItemRequest u where u.id = :id", ItemRequest.class);
        ItemRequest checkItemRequest = query.setParameter("id", retItemRequestDto.getId())
                .getSingleResult();

        assertThat(checkItemRequest.getId(), notNullValue());
        assertThat(checkItemRequest.getDescription(), equalTo("Computer"));
        assertThat(checkItemRequest.getRequestor().getId(), equalTo(retUserDto.getId()));
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(description);
        return dto;
    }

    @Test
    void testSaveBooking() {

        UserDto retUserDto = userService.create(makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(makeItemDto("Computer", "apple", true), retUserDto.getId());

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

    private BookingDto makeBookingDtoDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingDto dto = new BookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);

        return dto;
    }
}
