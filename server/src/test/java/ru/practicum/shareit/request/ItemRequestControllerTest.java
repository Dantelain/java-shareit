package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.utils.ItemRequestGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ItemRequestControllerTest {

    private ItemRequestController itemRequestController;
    @Mock
    private ItemRequestService itemRequestService;

    @BeforeEach
    void setUp() {
        itemRequestController = new ItemRequestController(itemRequestService);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void createItemRequestOk() {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenAnswer(invocationOnMock -> {
            Long index = invocationOnMock.getArgument(0);
            return ItemRequestGenerator.getItemRequestDto(index, 3);
        });
        ItemRequestDto itemRequestDto = itemRequestController.createItemRequest(1L, ItemRequestGenerator.getItemRequestDto(1L, 3));
        assertEquals(1L, itemRequestDto.getId());
        verify(itemRequestService).createItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void getItemRequestByUserIdOk() {
        when(itemRequestService.getItemRequestByUserId(anyLong())).thenAnswer(invocationOnMock -> {
            Long index = invocationOnMock.getArgument(0);
            return ItemRequestGenerator.getItemRequestsDto(index.intValue(), 3);
        });
        List<ItemRequestDto> itemRequestDto = itemRequestController.getItemRequestByUserId(4L);
        assertEquals(4, itemRequestDto.size());
        verify(itemRequestService).getItemRequestByUserId(anyLong());
    }

    @Test
    void getItemRequestByIdOk() {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenAnswer(invocationOnMock -> {
            Long index = invocationOnMock.getArgument(1);
            return ItemRequestGenerator.getItemRequestDto(index, 3);
        });
        ItemRequestDto itemRequestDto = itemRequestController.getItemRequestById(2L, 2L);
        assertEquals(2L, itemRequestDto.getId());
        verify(itemRequestService).getItemRequestById(2L, 2L);
    }

    @Test
    void getAllItemRequest() {
        when(itemRequestService.getAllItemRequest(anyLong(), anyInt(), anyInt())).thenAnswer(invocationOnMock -> {
            Integer index = invocationOnMock.getArgument(2);
            return ItemRequestGenerator.getItemRequestsDto(index, 3);
        });
        List<ItemRequestDto> itemRequestDtoList = itemRequestController.getAllItemRequest(2L, 0, 4);
        assertEquals(4, itemRequestDtoList.size());
        verify(itemRequestService).getAllItemRequest(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getAllItemRequestNull() {
        when(itemRequestService.getAllItemRequest(anyLong(), anyInt(), anyInt())).thenAnswer(invocationOnMock -> {
            Integer index = invocationOnMock.getArgument(2);
            return ItemRequestGenerator.getItemRequestsDto(index, 3);
        });
        List<ItemRequestDto> itemRequestDto1List = itemRequestController.getAllItemRequest(2L, null, 3);
        assertEquals(0, itemRequestDto1List.size());
        List<ItemRequestDto> itemRequestDto2List = itemRequestController.getAllItemRequest(2L, 3, null);
        assertEquals(0, itemRequestDto2List.size());
        List<ItemRequestDto> itemRequestDto3List = itemRequestController.getAllItemRequest(2L, null, null);
        assertEquals(0, itemRequestDto3List.size());
        verifyNoInteractions(itemRequestService);
    }

}