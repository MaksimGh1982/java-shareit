import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shareit.ShareItApp;
import shareit.item.ItemService;
import shareit.item.dto.ItemDto;
import shareit.item.dto.ItemDtoWithBookComment;
import shareit.item.model.Item;
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
public class ItemTest {

    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void testSaveItem() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

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

    @Test
    void searchItem() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(makeItemDto("Computer", "apple", true), retUserDto.getId());

        Collection<ItemDto> itemsDto = itemService.searchItem("APP%");
        List<ItemDto> list = new ArrayList(itemsDto);

        assertThat(list.getFirst().getId(), equalTo(retItemDto.getId()));
        assertThat(list.getFirst().getName(), equalTo(retItemDto.getName()));
        assertThat(list.getFirst().getDescription(), equalTo(retItemDto.getDescription()));
    }

    @Test
    void testUpdateItem() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(makeItemDto("Computer", "apple", false), retUserDto.getId());

        itemService.update(makeItemDto("NewComputer", "Newapple", true), retItemDto.getId(), retUserDto.getId());

        TypedQuery<Item> query = em.createQuery("Select u from Item u where u.id = :id", Item.class);
        Item checkItem = query.setParameter("id", retItemDto.getId())
                .getSingleResult();

        assertThat(checkItem.getId(), notNullValue());
        assertThat(checkItem.getName(), equalTo("NewComputer"));
        assertThat(checkItem.getDescription(), equalTo("Newapple"));
        assertThat(checkItem.getAvailable(), equalTo(true));
        assertThat(checkItem.getOwner().getId(), equalTo(retItemDto.getOwner()));
    }

    @Test
    void findAllByUser() {

        UserDto retUserDto = userService.create(UserTest.makeUserDto("Ivanov Ivan", "some@email.com"));

        ItemDto retItemDto = itemService.create(makeItemDto("Computer", "apple", true), retUserDto.getId());

        Collection<ItemDtoWithBookComment> itemsDto = itemService.findAllByUser(retUserDto.getId());
        List<ItemDtoWithBookComment> list = new ArrayList(itemsDto);

        assertThat(list.getFirst().getId(), equalTo(retItemDto.getId()));
        assertThat(list.getFirst().getName(), equalTo(retItemDto.getName()));
        assertThat(list.getFirst().getDescription(), equalTo(retItemDto.getDescription()));
    }

    static ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);

        return dto;
    }
}

