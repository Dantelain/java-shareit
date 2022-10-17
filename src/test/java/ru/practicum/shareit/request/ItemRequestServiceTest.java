package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.ItemRequestGenerator;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void getByIdFail() {
        ItemRequestDto itemRequestDto = ItemRequestGenerator.getItemRequestDto(1L, 1);
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestByUserId(1L));
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllItemRequest(1L, 0, 10));
        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(1L, itemRequestDto));
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 1L));
    }
}
