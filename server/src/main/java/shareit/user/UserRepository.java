package shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select count(*) from User u where email = ?1 and id <> ?2")
    Integer CountEmail(String email, Long userId);

}
