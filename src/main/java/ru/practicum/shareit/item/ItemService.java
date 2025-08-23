package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemService(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage,
                       @Qualifier("InMemoryUserStorage") UserStorage userStorage,
                       ItemMapper itemMapper) {

        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
        this.userStorage = userStorage;
    }

    public Collection<ItemDto> findAllByUser(Long userId) {
        log.info("Поиск всех вещей пользователя id = {}", userId);
        return itemStorage.findAllByUser(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto create(@Valid ItemDto itemDto, Long userId) {
        log.info("Создать вещь = {}", itemDto);
        if (userStorage.findUserById(userId) == null) {
            throw new NotFoundException("пользователь с id = " + userId + " не найден");
        } else {
            itemDto.setOwner(userId);
            return itemMapper.toItemDto(itemStorage.create(itemMapper.toItem(itemDto)));
        }
    }

    public ItemDto update(ItemDto newItemDto, Long itemId, Long userId) {
        log.info("Обновить вещь id = {} пользователя = {}", itemId, userId);
        newItemDto.setId(itemId);
        newItemDto.setOwner(userId);

        Item oldItem = itemStorage.findItemById(itemId);

        if (oldItem == null) {
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        } else if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь может редактировать только свои вещи!");
        } else {
            return itemMapper.toItemDto(itemStorage.update(itemMapper.toItem(newItemDto)));
        }
    }

    public ItemDto findItemById(long id) {
        log.info("Поиск вещи id = {}", id);
        return itemMapper.toItemDto(itemStorage.findItemById(id));
    }

    public Collection<ItemDto> searchItem(String text) {
        log.info("Поиск вещи id = {}", text);
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        } else {
            return itemStorage.searchItem(text)
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
}
