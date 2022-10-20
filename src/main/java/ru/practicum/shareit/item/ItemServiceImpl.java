package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<ItemBookingDto> getListItemByUserId(Long userId) {
        List<ItemBookingDto> itemList = itemRepository.findByOwner(User.builder().id(userId).build(), Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(ItemMapper::toItemBookingDto)
                .collect(Collectors.toList());
        List<Long> listItemId = itemList.stream().map(ItemBookingDto::getId).collect(Collectors.toList());
        List<Comment> commentList = commentRepository.findAll(((root, query, criteriaBuilder) -> root.get("item").in(listItemId)));
        List<Booking> bookingList = bookingRepository.findAll(((root, query, criteriaBuilder) -> root.get("item").in(listItemId)), Sort.by(Sort.Direction.DESC, "dateStart"));
        itemList.forEach((it) -> {
            setLastAndNextBooking(bookingList.stream().filter((el) -> el.getItem().getId().equals(it.getId())).collect(Collectors.toList()), it);
            List<ItemBookingDto.Comment> commentDtoList = commentList.stream().map(ItemMapper::toItemBookingDtoComment).collect(Collectors.toList());
            it.setComments(commentDtoList);
        });
        return itemList;
    }

    @Override
    public ItemBookingDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Comment> commentList = commentRepository.findAll((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("item"), itemId));
        List<Booking> bookingList = bookingRepository.findAll((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("item"), itemId), Sort.by(Sort.Direction.DESC, "dateStart"));
        ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(item);
        List<ItemBookingDto.Comment> commentDtoList = commentList.stream().map(ItemMapper::toItemBookingDtoComment).collect(Collectors.toList());
        itemBookingDto.setComments(commentDtoList);
        if (item.getOwner().getId().equals(userId)) {
            setLastAndNextBooking(bookingList, itemBookingDto);
        }
        return itemBookingDto;
    }

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto, user);
        if (item.getName() == null || item.getName().isEmpty()) {
            throw new ValidationException("Название вещи не может быть пустым", "");
        }
        if (item.getDescription() == null || item.getDescription().isEmpty()) {
            throw new ValidationException("Описание вещи не может быть", "");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Доступность вещи не может быть пустой", "");
        }
        return ItemMapper.toItemDto(itemRepository.saveAndFlush(item));
    }

    @Transactional
    @Override
    public ItemDto editItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        Item itemNew = ItemMapper.toItem(itemDto, user);
        if (!item.getOwner().equals(itemNew.getOwner())) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (itemNew.getName() != null) {
            item.setName(itemNew.getName());
        }
        if (itemNew.getDescription() != null) {
            item.setDescription(itemNew.getDescription());
        }
        if (itemNew.getAvailable() != null) {
            item.setAvailable(itemNew.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.saveAndFlush(item));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isEmpty()) return new ArrayList<>();
        return itemRepository.searchText(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        LocalDateTime created = LocalDateTime.now();
        List<Booking> bookingList = bookingRepository.findAll(((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("item"), itemId),
                        criteriaBuilder.equal(root.get("booker"), userId),
                        criteriaBuilder.lessThan(root.get("dateStart"), created),
                        criteriaBuilder.equal(root.get("status"), Status.APPROVED)
                )));
        if (bookingList.size() > 0) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setCreated(created);
            comment.setAuthor(user);
            comment.setItem(item);
            return CommentMapper.toCommentDto(commentRepository.saveAndFlush(comment));
        } else {
            throw new ValidationException("Пользователь не брал вещи в аренду", userId.toString());
        }
    }

    private void setLastAndNextBooking(List<Booking> bookingList, ItemBookingDto itemBookingDto) {
        ItemBookingDto.BookingItem lastBooking = bookingList.stream()
                .filter((e) -> e.getDateEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .map(BookingMapper::toBookingItem)
                .orElse(null);
        ItemBookingDto.BookingItem nextBooking = bookingList.stream()
                .filter((e) -> e.getDateStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .map(BookingMapper::toBookingItem)
                .orElse(null);
        itemBookingDto.setLastBooking(lastBooking);
        itemBookingDto.setNextBooking(nextBooking);
    }
}
