package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ItemDto> getListItemByUserId(Long userId) {
        return itemRepository.getListItemByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Название вещи не может быть пустым", "");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Описание вещи не может быть", "");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность вещи не может быть пустой", "");
        }
        return ItemMapper.toItemDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userRepository.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        return ItemMapper.toItemDto(itemRepository.editItem(itemId, item));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
