package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Repository("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    // вспомогательный метод для генерации идентификатора
    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Item findItemById(long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        throw new NotFoundException("Вещь с id = " + id + " не найдена");
    }

    public Collection<Item> findAllByUser(Long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public Item create(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    public Collection<Item> searchItem(String text) {
        return items.values()
                .stream()
                .filter(item -> item.getAvailable() == true &&
                        (item.getName().toUpperCase().indexOf(text) >= 0 || item.getDescription().toUpperCase().indexOf(text) >= 0))
                .collect(Collectors.toList());
    }

    public Item update(Item newItem) {

        Item oldItem = items.get(newItem.getId());

        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }

        return oldItem;
    }
}
