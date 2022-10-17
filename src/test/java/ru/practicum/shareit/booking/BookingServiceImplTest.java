package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.BookingGenerator;
import ru.practicum.shareit.utils.ItemGenerator;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(bookingRepository, userRepository, itemRepository);
    }

    @Test
    void createBookingOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id + 1));
        });
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class))).thenReturn(new ArrayList<>());
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BookingDto bookingDto = bookingService.createBooking(1L, BookingGenerator.getCreateBookingDto(1L));
        assertEquals(1L, bookingDto.getId());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class));
        verify(bookingRepository).saveAndFlush(any(Booking.class));
    }

    @Test
    void createBookingFailDate() {
        CreateBookingDto createBookingDto = BookingGenerator.getCreateBookingDto(1L);
        createBookingDto.setStart(createBookingDto.getEnd().plusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, createBookingDto));
    }

    @Test
    void createBookingFailUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, BookingGenerator.getCreateBookingDto(1L)));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void createBookingFailItem() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, BookingGenerator.getCreateBookingDto(1L)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void createBookingFailAvailable() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Item item = ItemGenerator.getItem(id + 1);
            item.setAvailable(false);
            return Optional.of(item);
        });
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, BookingGenerator.getCreateBookingDto(1L)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void createBookingFailOwner() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id));
        });
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, BookingGenerator.getCreateBookingDto(1L)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void createBookingFailBooking() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(itemRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(ItemGenerator.getItem(id + 1));
        });
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class))).thenReturn(BookingGenerator.getBookings(3));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, BookingGenerator.getCreateBookingDto(1L)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class));
    }

    @Test
    void approvedBookingOk() {
        when(bookingRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Booking booking = BookingGenerator.getBooking(id);
            switch (id.intValue()) {
                case 1:
                    booking.setStatus(Status.APPROVED);
                    break;
                case 2:
                    booking.setStatus(Status.REJECTED);
                    break;
                case 3:
                    booking.setStatus(Status.WAITING);
                    break;
                default:
                    return Optional.empty();
            }
            return Optional.of(booking);
        });
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BookingDto bookingDto = bookingService.approvedBooking(3L, 3L, true);
        assertEquals(Status.APPROVED, bookingDto.getStatus());
        bookingDto = bookingService.approvedBooking(3L, 3L, false);
        assertEquals(Status.REJECTED, bookingDto.getStatus());
        bookingDto = bookingService.approvedBooking(2L, 2L, true);
        assertEquals(Status.APPROVED, bookingDto.getStatus());
        bookingDto = bookingService.approvedBooking(1L, 1L, false);
        assertEquals(Status.REJECTED, bookingDto.getStatus());
        verify(bookingRepository, times(4)).findById(anyLong());
        verify(bookingRepository, times(4)).saveAndFlush(any(Booking.class));
    }

    @Test
    void approvedBookingFail() {
        when(bookingRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Booking booking = BookingGenerator.getBooking(id);
            switch (id.intValue()) {
                case 1:
                    booking.setStatus(Status.APPROVED);
                    break;
                case 2:
                    booking.setStatus(Status.REJECTED);
                    break;
                case 3:
                    booking.setStatus(Status.WAITING);
                    break;
                default:
                    return Optional.empty();
            }
            return Optional.of(booking);
        });
        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(1L, 4L, true));
        assertThrows(ValidationException.class, () -> bookingService.approvedBooking(1L, 1L, true));
        assertThrows(ValidationException.class, () -> bookingService.approvedBooking(1L, 2L, false));
        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(1L, 3L, false));
        verify(bookingRepository, times(4)).findById(anyLong());
    }

    @Test
    void getBookingByIdOk() {
        when(bookingRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(BookingGenerator.getBooking(id));
        });
        BookingDto bookingDto = bookingService.getBookingById(1L, 1L);
        assertEquals(1L, bookingDto.getId());
        bookingDto = bookingService.getBookingById(11L, 1L);
        assertEquals(1L, bookingDto.getId());
        verify(bookingRepository, times(2)).findById(anyLong());
    }

    @Test
    void getBookingByIdFail() {
        when(bookingRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Booking booking = BookingGenerator.getBooking(id);
            if (id.intValue() == 1) {
                return Optional.of(booking);
            }
            return Optional.empty();
        });
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(2L, 1L));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(3L, 2L));
        verify(bookingRepository, times(2)).findById(anyLong());
    }

    @Test
    void getBookingAllOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class), any(Sort.class))).thenReturn(BookingGenerator.getBookings(3));
        List<BookingDto> bookingDtoList = bookingService.getBookingAll(1L, Status.WAITING.name(), true);
        assertEquals(3, bookingDtoList.size());
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class), any(Sort.class));
    }

    @Test
    void getBookingAllFail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getBookingAll(1L, Status.WAITING.name(), true));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingAll(1L, Status.WAITING.name(), true, 0, 10));
        verify(userRepository, times(2)).findById(anyLong());
    }

    @Test
    void getBookingAllPageOk() {
        when(userRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return Optional.of(UserGenerator.getUser(id));
        });
        when(bookingRepository.findAll((Specification<Booking>) any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(BookingGenerator.getBookings(3)));
        List<BookingDto> bookingDtoList = bookingService.getBookingAll(1L, Status.WAITING.name(), true, 0, 10);
        assertEquals(3, bookingDtoList.size());
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findAll((Specification<Booking>) any(Specification.class), any(Pageable.class));
    }


}