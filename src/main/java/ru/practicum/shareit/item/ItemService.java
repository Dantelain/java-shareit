package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getListItemByUserId(Long userId);

    ItemDto getItemById(Long itemId);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(String text);

}
