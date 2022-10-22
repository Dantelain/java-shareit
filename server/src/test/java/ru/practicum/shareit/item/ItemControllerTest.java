package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.CommentGenerator;
import ru.practicum.shareit.utils.ItemGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ItemControllerTest {

    private ItemController itemController;

    @Mock
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemController = new ItemController(itemService);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getListItemByUserIdOk() {
        when(itemService.getListItemByUserId(anyLong())).thenAnswer(invocation -> ItemGenerator.getItemBookingsDto(4));
        List<ItemBookingDto> itemBookingDtoList = itemController.getListItemByUserId(2L);
        assertEquals(4, itemBookingDtoList.size());
        verify(itemService).getListItemByUserId(anyLong());
    }

    @Test
    void getItemByIdOk() {
        when(itemService.getItemById(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return ItemGenerator.getItemBookingDto(userId);
        });
        ItemBookingDto itemBookingDto = itemController.getItemById(1L, 1L);
        assertEquals(1L, itemBookingDto.getId());
        verify(itemService).getItemById(anyLong(), anyLong());
    }

    @Test
    void createItemOk() {
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return ItemGenerator.getItemDto(userId);
        });
        ItemDto itemDto = itemController.createItem(2L, ItemGenerator.getItemDto(2L));
        assertEquals(2L, itemDto.getId());
        verify(itemService).createItem(anyLong(), any(ItemDto.class));
    }

    @Test
    void editItemOk() {
        when(itemService.editItem(anyLong(), anyLong(), any(ItemDto.class))).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return ItemGenerator.getItemDto(userId);
        });
        ItemDto itemDto = itemController.editItem(2L, 2L, ItemGenerator.getItemDto(2L));
        assertEquals(2L, itemDto.getId());
        verify(itemService).editItem(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void searchItemOk() {
        when(itemService.searchItem(anyString())).thenReturn(ItemGenerator.getItemsDto(4));
        List<ItemDto> itemDtoList = itemController.searchItem("Text Search");
        assertEquals(4, itemDtoList.size());
        verify(itemService).searchItem(anyString());
    }

    @Test
    void createCommentOk() {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(CommentGenerator.getCommentDto(2L));
        CommentDto commentDto = CommentGenerator.getCommentDto(2L);
        CommentDto commentDto1 = itemController.createComment(2L, 2L, commentDto);
        assertEquals(commentDto1.getAuthorName(), commentDto.getAuthorName());
        verify(itemService).createComment(anyLong(), anyLong(), any(CommentDto.class));
    }

}