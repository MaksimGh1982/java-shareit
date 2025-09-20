package shareit.user;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    User findUserById(long id);

    void deleteUser(long id);
}
