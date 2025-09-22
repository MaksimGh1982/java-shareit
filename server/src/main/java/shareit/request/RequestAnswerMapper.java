package shareit.request;

import lombok.experimental.UtilityClass;
import shareit.request.dto.ItemRequestAnswerDto;

import java.util.List;

@UtilityClass
public class RequestAnswerMapper {
    public ItemRequestAnswerDto toItemRequestAnswerDto(ItemRequest itemRequest,
                                                       List<RequestItemAnswer> requestItemAnswerList) {
        return new ItemRequestAnswerDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                requestItemAnswerList
        );
    }
}
