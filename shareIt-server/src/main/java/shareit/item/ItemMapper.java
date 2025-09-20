package shareit.item;

import lombok.experimental.UtilityClass;
import shareit.item.dto.ItemDto;
import shareit.item.model.Item;
import shareit.request.ItemRequest;
import shareit.user.User;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() //!= null ? item.getRequest()/*.getId()*/ : null
        );
    }

    public Item toItem(ItemDto itemDto, User user) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequestId()
        );
    }
}


