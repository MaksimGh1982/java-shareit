package gateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String SHARER_USER = "X-Sharer-User-Id";

    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(SHARER_USER) Long userId) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestByUser(@RequestHeader(SHARER_USER) Long userId) {
        return requestService.getRequestByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(SHARER_USER) Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId) {
        return requestService.getRequestById(requestId);
    }
}
