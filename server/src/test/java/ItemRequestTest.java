import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shareit.ShareItApp;
import shareit.request.ItemRequest;
import shareit.request.RequestService;
import shareit.request.dto.ItemRequestAnswerDto;
import shareit.request.dto.ItemRequestDto;
import shareit.user.UserService;
import shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "application-test.properties",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShareItApp.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestTest {

    private final EntityManager em;
    private final UserService userService;
    private final RequestService requestService;

    @Test
    void testSaveRequest() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemRequestDto retItemRequestDto = requestService.create(makeItemRequestDto("Computer"), retUserDto.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select u from ItemRequest u where u.id = :id", ItemRequest.class);
        ItemRequest checkItemRequest = query.setParameter("id", retItemRequestDto.getId())
                .getSingleResult();

        assertThat(checkItemRequest.getId(), notNullValue());
        assertThat(checkItemRequest.getDescription(), equalTo("Computer"));
        assertThat(checkItemRequest.getRequestor().getId(), equalTo(retUserDto.getId()));
    }

    @Test
    void testGetAllRequests() {
        UserDto retUserDto = userService.create(UserTest.makeUserDto("Petrov Petr", "some1@email.com"));
        UserDto retUserDto1 = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemRequestDto retItemRequestDto = requestService.create(makeItemRequestDto("Computer"), retUserDto.getId());

        Collection<ItemRequestDto> itemRequestsDto = requestService.getAllRequests(retUserDto1.getId());
        List<ItemRequestDto> list = new ArrayList(itemRequestsDto);

        assertThat(list.getFirst().getId(), equalTo(retItemRequestDto.getId()));
        assertThat(list.getFirst().getDescription(), equalTo(retItemRequestDto.getDescription()));
        assertThat(list.getFirst().getRequestorId(), equalTo(retItemRequestDto.getRequestorId()));
    }

    @Test
    void getRequestById() {
        UserDto retUserDto = userService.create(UserTest.makeUserDto("Petrov Petr", "some1@email.com"));

        ItemRequestDto retItemRequestDto = requestService.create(makeItemRequestDto("Computer"), retUserDto.getId());

        ItemRequestAnswerDto itemRequestAnswerDto = requestService.getRequestById(retItemRequestDto.getId());

        assertThat(itemRequestAnswerDto.getId(), equalTo(retItemRequestDto.getId()));
        assertThat(itemRequestAnswerDto.getDescription(), equalTo(retItemRequestDto.getDescription()));
        assertThat(itemRequestAnswerDto.getRequestorId(), equalTo(retItemRequestDto.getRequestorId()));
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription(description);
        return dto;
    }
}

