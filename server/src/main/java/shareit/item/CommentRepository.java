package shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemId(Long id);

    List<Comment> findByItemOwnerId(Long id);
}


