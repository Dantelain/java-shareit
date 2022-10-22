package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.BookingGenerator;
import ru.practicum.shareit.utils.CommentGenerator;
import ru.practicum.shareit.utils.ItemGenerator;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository, commentRepository);
    }

    @Test
    void getListItemByUserIdOk() {
        when(itemRepository.findByOwner(any(User.class), any(Sort.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return ItemGenerator.getItems(4);
        });
        when(commentRepository.findAll((Specification<Comment>) any(Specification.class))).thenReturn(CommentGenerator.getComments(4));
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class), any(Sort.class))).thenReturn(BookingGenerator.getBookings(4));
        List<ItemBookingDto> itemBookingDtoList = itemService.getListItemByUserId(1L);
        assertEquals(4, itemBookingDtoList.size());
        verify(itemRepository).findByOwner(any(User.class), any(Sort.class));
        verify(commentRepository).findAll((Specification<Comment>) any(Specification.class));
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class), any(Sort.class));
    }

    @Test
    void getItemByIdOk() {
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id));
        });
        when(commentRepository.findAll((Specification<Comment>) any(Specification.class))).thenReturn(CommentGenerator.getComments(3));
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class), any(Sort.class))).thenReturn(BookingGenerator.getBookings(3));
        ItemBookingDto itemBookingDto = itemService.getItemById(1L, 1L);
        assertEquals(3, itemBookingDto.getComments().size());
        verify(itemRepository).findById(anyLong());
        verify(commentRepository).findAll((Specification<Comment>) any(Specification.class));
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class), any(Sort.class));
    }

    @Test
    void createItemOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto itemDto = itemService.createItem(1L, ItemGenerator.getItemDto(1L));
        assertEquals(1L, itemDto.getId());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).saveAndFlush(any(Item.class));
    }

    @Test
    void createItemFail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, ItemGenerator.getItemDto(1L)));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void editItemOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id));
        });
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto itemDtoNew = ItemGenerator.getItemDto(1L);
        ItemDto itemDto = itemService.editItem(1L, 1L, itemDtoNew);
        assertEquals(1L, itemDto.getId());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(itemRepository).saveAndFlush(any(Item.class));
    }

    @Test
    void editItemNotOwner() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id));
        });
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto itemDtoNew = ItemGenerator.getItemDto(1L);
        assertThrows(NotFoundException.class, () -> itemService.editItem(2L, 1L, itemDtoNew));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void editItemOkNull() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id));
        });
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ItemDto itemDtoNew = ItemGenerator.getItemDto(1L);
        itemDtoNew.setDescription(null);
        itemDtoNew.setName(null);
        itemDtoNew.setAvailable(null);
        ItemDto itemDto = itemService.editItem(1L, 1L, itemDtoNew);
        assertEquals(1L, itemDto.getId());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(itemRepository).saveAndFlush(any(Item.class));
    }

    @Test
    void searchItemOk() {
        when(itemRepository.searchText(anyString())).thenReturn(ItemGenerator.getItems(5));
        List<ItemDto> itemDtoList = itemService.searchItem("search text");
        assertEquals(5, itemDtoList.size());
        verify(itemRepository).searchText(anyString());
    }

    @Test
    void searchItemFail() {
        List<ItemDto> itemDtoList = itemService.searchItem(null);
        assertEquals(0, itemDtoList.size());
        itemDtoList = itemService.searchItem("");
        assertEquals(0, itemDtoList.size());
    }

    @Test
    void createCommentOk() {
        AtomicReference<Long> itemId = new AtomicReference<>();
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            itemId.set(invocation.getArgument(0));
            return Optional.of(ItemGenerator.getItem(itemId.get()));
        });
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class))).thenReturn(BookingGenerator.getBookings(3));
        when(commentRepository.saveAndFlush(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            return CommentGenerator.getComment(comment.getId());
        });
        CommentDto commentDtoCreate = CommentGenerator.getCommentDto(1L);
        CommentDto commentDto = itemService.createComment(1L, 1L, commentDtoCreate);
        assertEquals(1L, commentDto.getId());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class));
        verify(commentRepository).saveAndFlush(any(Comment.class));
    }

    @Test
    void createCommentFailUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.createComment(1L, 1L, CommentGenerator.getCommentDto(1L)));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void createCommentFailItem() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.createComment(1L, 1L, CommentGenerator.getCommentDto(1L)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void createCommentFailBooking() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id));
        });
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class))).thenReturn(new ArrayList<>());
        assertThrows(ValidationException.class, () -> itemService.createComment(1L, 1L, CommentGenerator.getCommentDto(1L)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class));
    }

}