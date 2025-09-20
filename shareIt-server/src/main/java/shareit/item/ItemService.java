package shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.DateBooking;
import shareit.booking.BookGetStatus;
import shareit.booking.Booking;
import shareit.booking.BookingRepository;
import shareit.booking.BookingService;
import shareit.exception.NotFoundException;
import shareit.exception.ValidException;
import shareit.item.dto.CommentDto;
import shareit.item.dto.ItemDto;
import shareit.item.dto.ItemDtoWithBookComment;
import shareit.item.model.Comment;
import shareit.item.model.Item;
import shareit.request.RequestRepository;
import shareit.user.User;
import shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository,
                       UserRepository userRepository,
                       BookingService bookingService,
                       CommentRepository commentRepository,
                       BookingRepository bookingRepository,
                       RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
        this.requestRepository = requestRepository;
    }

    public Collection<ItemDtoWithBookComment> findAllByUser(Long userId) {
        log.info("Поиск всех вещей пользователя id = {}", userId);

        List<String> comments = commentRepository.findByItemOwnerId(userId)
                .stream()
                .map(Comment::getText)
                .collect(Collectors.toList());

        List<Booking> bookings = bookingRepository.findByItemOwnerId(userId);

        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(item -> {
                    Booking prevBooking = bookings
                            .stream()
                            .filter(booking -> booking.getItem().getId().equals(item.getId()))
                            .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                            .sorted(Comparator.comparing(Booking::getStart).reversed())
                            .findFirst()
                            .orElse(null);
                    DateBooking prevDateBooking = null;
                    if (prevBooking != null) {
                        prevDateBooking = new DateBooking(prevBooking.getStart(), prevBooking.getEnd());
                    }

                    Booking nextBooking = bookingRepository.findByItemId(item.getId())
                            .stream()
                            .filter(booking -> booking.getItem().getId().equals(item.getId()))
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .sorted(Comparator.comparing(Booking::getStart))
                            .findFirst()
                            .orElse(null);
                    DateBooking nextDateBooking = null;
                    if (nextBooking != null) {
                        nextDateBooking = new DateBooking(nextBooking.getStart(), nextBooking.getEnd());
                    }
                    return ItemDtoWithBookCommentMapper.toItemDtoWithBookComment(item, prevDateBooking, nextDateBooking, comments);
                })
                .collect(Collectors.toList());
    }

    public ItemDto create(@Valid ItemDto itemDto, Long userId) {
        log.info("Создать вещь = {}", itemDto);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("пользователь с id = " + userId + " не найден");
        } else {
            //ItemRequest itemRequest = requestRepository.findById(itemDto.getRequest()).orElse(null);
            itemDto.setOwner(userId);
            return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user)));
        }
    }

    public ItemDto update(ItemDto newItemDto, Long itemId, Long userId) {
        log.info("Обновить вещь id = {} пользователя = {}", itemId, userId);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException("пользователь с id = " + userId + " не найден");
        }

        Item oldItem = itemRepository.findById(itemId).orElse(null);

        if (oldItem == null) {
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        } else if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь может редактировать только свои вещи!");
        } else {
            if (newItemDto.getName() != null) {
                oldItem.setName(newItemDto.getName());
            }
            if (newItemDto.getDescription() != null) {
                oldItem.setDescription(newItemDto.getDescription());
            }
            if (newItemDto.getAvailable() != null) {
                oldItem.setAvailable(newItemDto.getAvailable());
            }
            return ItemMapper.toItemDto(itemRepository.save(oldItem));
        }
    }

    public ItemDtoWithBookComment findItemById(long id) {
        log.info("Поиск вещи id = {}", id);
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            throw new NotFoundException("Вещь с id = " + id + " не найдена");
        }

        List<String> comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(Comment::getText)
                .collect(Collectors.toList());

        return ItemDtoWithBookCommentMapper.toItemDtoWithBookComment(item, null, null, comments);
    }

    public Collection<ItemDto> searchItem(String text) {
        log.info("Поиск вещи id = {}", text);
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        } else {
            return itemRepository.findByNameIgnoreCaseOrDescriptionIgnoreCaseContaining(text, text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    public CommentDto addComment(CommentDto newCommentDto, long itemId, long userId) {
        log.info("Добавление комментария к вещи id = {} пользователем = {}", itemId, userId);
        if (bookingService.getAllBookingByUser(userId, BookGetStatus.PAST)
                .stream()
                .filter(booking -> booking.getItem().getId() == itemId)
                .count() == 0) {
            throw new ValidException("Бронирование вещи id = " + itemId + " не найдено");
        }
        newCommentDto.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(newCommentDto,
                itemRepository.findById(itemId).get(),
                userRepository.findById(userId).get())));
    }
}
