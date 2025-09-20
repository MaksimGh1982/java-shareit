package shareit.request;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.RequestItemAnswer;
import shareit.exception.NotFoundException;
import shareit.item.ItemRepository;
import shareit.request.dto.ItemRequestAnswerDto;
import shareit.request.dto.ItemRequestDto;
import shareit.user.User;
import shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class RequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(UserRepository userRepository,
                          RequestRepository requestRepository,
                          ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
    }

    public ItemRequestDto create(@Valid ItemRequestDto itemRequestDto, Long userId) {
        log.info("Создать запрос на вещь");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        itemRequestDto.setRequestorId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        return RequestMapper.toItemRequestDto(requestRepository.save(RequestMapper.toItemRequest(itemRequestDto, user)));
    }

    public Collection<ItemRequestAnswerDto> getRequestByUser(Long userId) {
        log.info("Просмотр всех запросов пользователя id = {}", userId);
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return requestRepository.findByRequestorId(userId)
                .stream()
                .map(itemRequest -> {
                    List<RequestItemAnswer> requestItemAnswerList = itemRepository.findByRequest(itemRequest.getId())
                            .stream()
                            .map(item -> new RequestItemAnswer(item.getId(), item.getName(), item.getOwner().getId()))
                            .collect(Collectors.toList());
                    return RequestAnswerMapper.toItemRequestAnswerDto(itemRequest, requestItemAnswerList);
                })
                .collect(Collectors.toList());

    }

    public Collection<ItemRequestDto> getAllRequests(Long userId) {
        log.info("Просмотреть все запросы");
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("пользователь с id = " + userId + " не найден");
        }
        return requestRepository.findAll()
                .stream()
                .filter(itemRequest -> itemRequest.getRequestor().getId() != userId)
                .map(RequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    public ItemRequestAnswerDto getRequestById(long requestId) {
        log.info("Просмотр запроса id = {}", requestId);
        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Не найден запрос id = " + requestId));
        List<RequestItemAnswer> requestItemAnswerList = itemRepository.findByRequest(requestId)
                .stream()
                .map(item -> new RequestItemAnswer(item.getId(), item.getName(), item.getOwner().getId()))
                .collect(Collectors.toList());
        return RequestAnswerMapper.toItemRequestAnswerDto(itemRequest, requestItemAnswerList);
    }
}
