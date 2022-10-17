package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.itemRequestRepository = itemRequestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setAuthorRequest(user);
        itemRequestRepository.saveAndFlush(itemRequest);
        return ItemRequestMapper.toItemRequestDto(itemRequest, new ArrayList<>());
    }

    @Override
    public List<ItemRequestDto> getItemRequestByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByAuthorRequest(user, Sort.by(Sort.Direction.DESC, "created"));
        List<Long> itemRequestIdList = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemDto> itemList = itemRepository.findAll((root, query, criteriaBuilder) -> root.get("request").in(itemRequestIdList))
                .stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        return itemRequestList
                .stream()
                .map((itemRequest) -> ItemRequestMapper.toItemRequestDto(itemRequest, getItemDtoList(itemList, itemRequest)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequest(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        int page = from / size;
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("authorRequest"), user),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created")));
        List<Long> itemRequestIdList = itemRequestPage.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<ItemDto> itemList = itemRepository.findAll((root, query, criteriaBuilder) -> root.get("request").in(itemRequestIdList))
                .stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        return itemRequestRepository.findAll(
                        (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("authorRequest"), user),
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map((itemRequest) -> ItemRequestMapper.toItemRequestDto(itemRequest, getItemDtoList(itemList, itemRequest)))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<ItemDto> itemList = itemRepository.findAll((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("request"), itemRequest.getId()))
                .stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemList);
    }

    private List<ItemDto> getItemDtoList(List<ItemDto> itemList, ItemRequest itemRequest) {
        return itemList.stream().filter((el) -> el.getRequestId().equals(itemRequest.getId())).collect(Collectors.toList());
    }
}
