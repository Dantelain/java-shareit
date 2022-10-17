package ru.practicum.shareit.utils;

import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestGenerator {

    public static ItemRequest getItemRequest() {
        return getItemRequest(1L);
    }

    public static ItemRequest getItemRequest(Long id) {
        return ItemRequest.builder()
                .id(id)
                .description("Описание запроса #" + id)
                .authorRequest(UserGenerator.getUser(id))
                .created(LocalDateTime.now())
                .build();
    }

    public static List<ItemRequest> getItemRequests(int count) {
        var listOfItemRequests = new ArrayList<ItemRequest>();
        for (int i = 0; i < count; i++) {
            Long j = (long) (i + 1);
            listOfItemRequests.add(getItemRequest(j));
        }
        return listOfItemRequests;
    }

    public static ItemRequestDto getItemRequestDto(Long id, int itemCount) {
        return ItemRequestMapper.toItemRequestDto(getItemRequest(id), ItemGenerator.getItemsDto(itemCount));
    }

    public static List<ItemRequestDto> getItemRequestsDto(int count, int itemCount) {
        return getItemRequests(count).stream().map(e -> ItemRequestMapper.toItemRequestDto(e, ItemGenerator.getItemsDto(itemCount))).collect(Collectors.toList());
    }

}
