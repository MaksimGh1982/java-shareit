package shareit.request;

import lombok.experimental.UtilityClass;
import shareit.request.dto.ItemRequestDto;
import shareit.user.User;

@UtilityClass
public class RequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated()
        );
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                user,
                itemRequestDto.getCreated()
        );
    }
}
