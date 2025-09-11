package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select u from Item u where (upper(u.name) like ?1 or upper(u.description) like ?2) and available = true")
    List<Item> findByNameIgnoreCaseOrDescriptionIgnoreCaseContaining(String nameSearch, String descriptionSearch);

    List<Item> findByOwnerId(Long id);
}