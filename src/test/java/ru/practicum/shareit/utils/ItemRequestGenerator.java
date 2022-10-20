package ru.practicum.shareit.utils;

import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ItemRequestGenerator {

    public static ItemRequest getItemRequest(Long id) {
        return ItemRequest.builder()
                .id(id)
                .description("Описание запроса #" + id)
                .authorRequest(UserGenerator.getUser(id))
                .created(LocalDateTime.now())
                .build();
    }

    public static List<ItemRequest> getItemRequests(int count) {
        AtomicLong id = new AtomicLong(0L);
        return Arrays.stream(new Integer[count])
                .map((i) -> getItemRequest(id.incrementAndGet()))
                .collect(Collectors.toList());
    }

    public static ItemRequestDto getItemRequestDto(Long id, int itemCount) {
        return ItemRequestMapper.toItemRequestDto(getItemRequest(id), ItemGenerator.getItemsDto(itemCount));
    }

    public static List<ItemRequestDto> getItemRequestsDto(int count, int itemCount) {
        return getItemRequests(count).stream().map(e -> ItemRequestMapper.toItemRequestDto(e, ItemGenerator.getItemsDto(itemCount))).collect(Collectors.toList());
    }

}
