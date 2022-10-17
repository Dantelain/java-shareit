package ru.practicum.shareit.utils;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemGenerator {

    public static Item getItem() {
        return getItem(1L);
    }

    public static Item getItem(Long id) {
        return Item.builder()
                .id(id)
                .name("Вещь #" + id)
                .owner(UserGenerator.getUser(id))
                .description("Описание вещи #" + id)
                .available(true)
                .request(id)
                .build();
    }

    public static ItemDto getItemDto() {
        return ItemMapper.toItemDto(getItem());
    }

    public static ItemDto getItemDto(Long id) {
        return ItemMapper.toItemDto(getItem(id));
    }

    public static ItemBookingDto getItemBookingDto(Long id) {
        return ItemMapper.toItemBookingDto(getItem(id));
    }

    public static List<Item> getItems(int count) {
        var listOfItem = new ArrayList<Item>();
        for (int i = 0; i < count; i++) {
            Long j = (long) (i + 1);
            listOfItem.add(getItem(j));
        }
        return listOfItem;
    }

    public static List<ItemDto> getItemsDto(int count) {
        return getItems(count).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public static List<ItemBookingDto> getItemBookingsDto(int count) {
        return getItems(count).stream().map(ItemMapper::toItemBookingDto).collect(Collectors.toList());
    }
}
