package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemBookingDto> getListItemByUserId(Long userId);

    ItemBookingDto getItemById(Long userId, Long itemId);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> searchItem(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
