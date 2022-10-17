package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ItemGenerator;
import ru.practicum.shareit.utils.ItemRequestGenerator;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void createItemRequestOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(2L, ItemRequestGenerator.getItemRequestDto(2L, 2));
        assertEquals(2L, itemRequestDto.getId());
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository).saveAndFlush(any(ItemRequest.class));
    }

    @Test
    void createItemRequestFail() {
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));
        var ex = assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(2L, ItemRequestGenerator.getItemRequestDto(2L, 2)));
        assertEquals("Пользователь не найден", ex.getMessage());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void getItemRequestByUserIdOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRequestRepository.findAllByAuthorRequest(any(User.class), any(Sort.class))).thenReturn(ItemRequestGenerator.getItemRequests(3));
        when(itemRepository.findAll((Specification<Item>) any(Specification.class))).thenReturn(ItemGenerator.getItems(3));
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getItemRequestByUserId(2L);
        assertEquals(3, itemRequestDtoList.size());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findAll((Specification<Item>) any(Specification.class));
        verify(itemRequestRepository).findAllByAuthorRequest(any(User.class), any(Sort.class));
    }

    @Test
    void getAllItemRequestOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        Page<ItemRequest> page = new PageImpl<>(ItemRequestGenerator.getItemRequests(5));
        when(itemRequestRepository.findAll((Specification<ItemRequest>) any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(itemRepository.findAll((Specification<Item>) any(Specification.class))).thenReturn(ItemGenerator.getItems(5));
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllItemRequest(2L, 1, 10);
        assertEquals(5, itemRequestDtoList.size());
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository, times(2)).findAll((Specification<ItemRequest>) any(Specification.class), any(Pageable.class));
        verify(itemRepository).findAll((Specification<Item>) any(Specification.class));
    }

    @Test
    void getItemRequestByIdOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(userId));
        });
        when(itemRequestRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemRequestGenerator.getItemRequest(id));
        });
        when(itemRepository.findAll((Specification<Item>) any(Specification.class))).thenReturn(ItemGenerator.getItems(5));
        ItemRequestDto itemRequestDto = itemRequestService.getItemRequestById(2L, 2L);
        assertEquals(2L, itemRequestDto.getId());
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository).findById(anyLong());
        verify(itemRepository).findAll((Specification<Item>) any(Specification.class));
    }
}