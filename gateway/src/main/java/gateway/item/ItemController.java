package gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> findAllByUser(@RequestHeader(SHARER_USER) Long userId) {
        return itemService.findAllByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ItemDto itemDto, @RequestHeader(SHARER_USER) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto newItemDto,
                                         @PathVariable long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.update(newItemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable long itemId) {
        return itemService.findItemById(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody CommentDto newCommentDto,
                                             @PathVariable long itemId,
                                             @RequestHeader(SHARER_USER) Long userId) {
        return itemService.addComment(newCommentDto, itemId, userId);
    }
}

