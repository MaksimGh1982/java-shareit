package shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import shareit.request.dto.ItemRequestAnswerDto;
import shareit.request.dto.ItemRequestDto;

import java.util.Collection;


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
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader(SHARER_USER) Long userId) {
        return requestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public Collection<ItemRequestAnswerDto> getRequestByUser(@RequestHeader(SHARER_USER) Long userId) {
        return requestService.getRequestByUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests(@RequestHeader(SHARER_USER) Long userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestAnswerDto getRequestById(@PathVariable long requestId) {
        return requestService.getRequestById(requestId);
    }
}
