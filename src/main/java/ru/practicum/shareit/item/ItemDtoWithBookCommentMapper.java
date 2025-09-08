package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.DateBooking;
import ru.practicum.shareit.item.dto.ItemDtoWithBookComment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemDtoWithBookCommentMapper {

    public static ItemDtoWithBookComment toItemDtoWithBookComment(Item item, DateBooking beforeBook,
                                                                  DateBooking nextBook, List<String> comments) {
        return new ItemDtoWithBookComment(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                null,
                beforeBook,
                nextBook,
                comments
        );
    }
}
