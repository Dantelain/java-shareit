package ru.practicum.shareit.item;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getListItemByUserId(Long userId);

    Item getItemById(Long itemId);

    Item createItem(Item item);

    Item editItem(Long itemId, Item item);

    List<Item> searchItem(String text);

}
