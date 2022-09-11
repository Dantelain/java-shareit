package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> itemMap;
    private Long counter;

    public ItemRepositoryImpl() {
        this.itemMap = new HashMap<>();
        this.counter = 1L;
    }

    @Override
    public List<Item> getListItemByUserId(Long userId) {
        return itemMap.values()
                .stream()
                .filter(el -> el.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = itemMap.get(itemId);
        if (item == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        return item;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(counter);
        itemMap.put(counter, item);
        counter++;
        return item;
    }

    @Override
    public Item editItem(Long itemId, Item itemNew) {
        Item item = itemMap.get(itemId);
        if (!item.getOwner().equals(itemNew.getOwner())) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (itemNew.getName() != null) {
            item.setName(itemNew.getName());
        }
        if (itemNew.getDescription() != null) {
            item.setDescription(itemNew.getDescription());
        }
        if (itemNew.getAvailable() != null) {
            item.setAvailable(itemNew.getAvailable());
        }
        itemMap.put(itemId, item);
        return item;
    }

    @Override
    public List<Item> searchItem(String text) {
        return itemMap.values()
                .stream()
                .filter(el -> (!text.isEmpty() && (el.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                        el.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
