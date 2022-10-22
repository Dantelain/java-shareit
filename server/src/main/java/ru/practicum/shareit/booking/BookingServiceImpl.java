package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StateConversionFailedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto createBooking(Long userId, CreateBookingDto createBookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(createBookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна", item.getAvailable().toString());
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может брать вещь в аренду");
        }
        //Список бронирований пересекающийся по датам начала и окончания с вещью заправшиваемой в createBookingDto
        List<Booking> bookingList = bookingRepository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.or(
                                criteriaBuilder.and(
                                        criteriaBuilder.lessThan(root.get("dateStart"), createBookingDto.getStart()),
                                        criteriaBuilder.greaterThan(root.get("dateEnd"), createBookingDto.getStart())
                                ),
                                criteriaBuilder.and(
                                        criteriaBuilder.lessThan(root.get("dateStart"), createBookingDto.getEnd()),
                                        criteriaBuilder.greaterThan(root.get("dateEnd"), createBookingDto.getEnd())
                                )
                        ),
                        criteriaBuilder.equal(root.get("item"), createBookingDto.getItemId())
                ));
        if (bookingList.size() > 0) {
            throw new ValidationException("Бронирование вещи в запрашиваемые даты не доступно", item.getAvailable().toString());
        }
        Booking booking = BookingMapper.toBooking(createBookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getStatus().equals(Status.APPROVED) && approved || booking.getStatus().equals(Status.REJECTED) && !approved) {
            throw new ValidationException("Бронирование уже подтверждено или отклонено", "bookingId - " + bookingId);
        }
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(bookingRepository.saveAndFlush(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Пользователь не является владельцем вещи или бронирования");
        }
    }

    @Override
    public List<BookingDto> getBookingAll(Long userId, String state, Boolean owner) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return bookingRepository.findAll((root, query, criteriaBuilder) ->
                        criteriaBuilder.and(
                                owner ? root.get("item").in(getItemIdList(user)) : criteriaBuilder.equal(root.get("booker"), user.getId()),
                                getStatePredicate(state, root, criteriaBuilder)
                        ), Sort.by(Sort.Direction.DESC, "dateStart"))
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingAll(Long userId, String state, Boolean owner, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        int page = from / size;
        return bookingRepository.findAll((root, query, criteriaBuilder) ->
                        criteriaBuilder.and(
                                owner ? root.get("item").in(getItemIdList(user)) : criteriaBuilder.equal(root.get("booker"), user.getId()),
                                getStatePredicate(state, root, criteriaBuilder)
                        ), PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateStart")))
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private Predicate getStatePredicate(String state, Root<Booking> root, CriteriaBuilder criteriaBuilder) {
        try {
            State stateValue = State.valueOf(state);
            LocalDateTime currentDate = LocalDateTime.now();
            Predicate predicate = root.isNotNull();
            switch (stateValue) {
                //Текущие бронирования
                case CURRENT:
                    predicate =
                            criteriaBuilder.and(
                                    criteriaBuilder.lessThan(root.get("dateStart"), currentDate),
                                    criteriaBuilder.greaterThan(root.get("dateEnd"), currentDate)
                            );
                    break;
                //Будущие бронирования
                case FUTURE:
                    predicate = criteriaBuilder.greaterThan(root.get("dateStart"), currentDate);
                    break;
                //Завершенные бронирования
                case PAST:
                    predicate =
                            criteriaBuilder.and(
                                    criteriaBuilder.lessThan(root.get("dateStart"), currentDate),
                                    criteriaBuilder.lessThan(root.get("dateEnd"), currentDate),
                                    criteriaBuilder.equal(root.get("status"), Status.APPROVED)
                            );
                    break;
                //Бронирования со статусом ожидающие
                case WAITING:
                    predicate = criteriaBuilder.equal(root.get("status"), Status.WAITING);
                    break;
                //Бронирования со статусом отклоненные
                case REJECTED:
                    predicate = criteriaBuilder.equal(root.get("status"), Status.REJECTED);
                    break;
            }
            return predicate;
        } catch (IllegalArgumentException e) {
            throw new StateConversionFailedException(state);
        }
    }

    private List<Long> getItemIdList(User user) {
        return itemRepository.findByOwner(user, Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
    }

}
