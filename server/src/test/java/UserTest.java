import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shareit.ShareItApp;
import shareit.user.User;
import shareit.user.UserService;
import shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "application-test.properties",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShareItApp.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserTest {

    private final EntityManager em;
    private final UserService userService;

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

    @Test
    void testUpdateUser() {

        UserDto newUserDto = userService.create(makeUserDto("Ivanov Ivan", "some@email.com"));

        UserDto updUserDto = userService.update(makeUserDto("New Ivanov Ivan", "some1@email.com"), newUserDto.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", newUserDto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo("New Ivanov Ivan"));
        assertThat(user.getEmail(), equalTo("some1@email.com"));
    }

    @Test
    void testFindUser() {

        UserDto userDto = userService.create(makeUserDto("Ivanov Ivan", "some@email.com"));

        UserDto newUserDto = userService.findUserById(userDto.getId());

        assertThat(newUserDto.getId(), notNullValue());
        assertThat(newUserDto.getName(), equalTo("Ivanov Ivan"));
        assertThat(newUserDto.getEmail(), equalTo("some@email.com"));
    }

    static UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }


}
