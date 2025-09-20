package shareit.item;

import lombok.experimental.UtilityClass;
import shareit.item.dto.CommentDto;
import shareit.item.model.Comment;
import shareit.item.model.Item;
import shareit.user.User;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthorName().getName(),
                comment.getCreated()
        );
    }

    public Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                commentDto.getCreated()
        );
    }
}
