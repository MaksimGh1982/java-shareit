package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemDtoWithBookComment;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String SHARER_USER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemDtoWithBookComment> findAllByUser(@RequestHeader(SHARER_USER) Long userId) {
        return itemService.findAllByUser(userId);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto, @RequestHeader(SHARER_USER) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto newItemDto,
                          @PathVariable long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(newItemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookComment findItemBuId(@PathVariable long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto newCommentDto,
                                 @PathVariable long itemId,
                                 @RequestHeader(SHARER_USER) Long userId) {
        return itemService.addComment(newCommentDto, itemId, userId);
    }
}

