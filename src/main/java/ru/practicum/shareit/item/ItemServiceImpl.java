package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService{

    private final Map<Long, Item> itemMap;
    private Long counter;

    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.itemMap = new HashMap<>();
        this.userService = userService;
        this.counter = 1L;
    }

    @Override
    public List<ItemDto> getListItemByUserId(Long userId) {
        return itemMap.values()
                .stream()
                .filter(el->el.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemMap.get(itemId));
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        Item item = ItemMapper.toItem(itemDto, user);
        if (item.getName() == null || item.getName().isEmpty())
            throw new ValidationException("Название вещи не может быть пустым", "");
        if (item.getDescription() == null || item.getDescription().isEmpty())
            throw new ValidationException("Описание вещи не может быть", "");
        if (item.getAvailable() == null)
            throw new ValidationException("Доступность вещи не может быть пустой", "");
        item.setId(counter);
        itemMap.put(counter,item);
        counter++;
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        Item item = itemMap.get(itemId);
        if (!item.getOwner().equals(user))
            throw new NotFoundException("Пользователь не является владельцем вещи");
        Item itemNew = ItemMapper.toItem(itemDto, user);
        if (itemNew.getName() != null)
            item.setName(itemNew.getName());
        if (itemNew.getDescription() != null)
            item.setDescription(itemNew.getDescription());
        if (itemNew.getAvailable() != null)
            item.setAvailable(itemNew.getAvailable());
        itemMap.put(itemId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemMap.values()
                .stream()
                .filter(el-> (!text.isEmpty() && (el.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                        el.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)))))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
