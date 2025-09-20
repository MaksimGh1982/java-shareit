package shareit.item;

import shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item findItemById(long id);

    Collection<Item> findAllByUser(Long userId);

    Item create(Item item);

    Item update(Item item);

    Collection<Item> searchItem(String text);

}
