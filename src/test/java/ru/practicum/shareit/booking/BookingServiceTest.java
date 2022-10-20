package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.BookingGenerator;
import ru.practicum.shareit.utils.ItemGenerator;
import ru.practicum.shareit.utils.UserGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Rollback
@Transactional
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;


    @Test
    void bookingTestOk() {
        userRepository.saveAndFlush(UserGenerator.getUser(1L));
        userRepository.saveAndFlush(UserGenerator.getUser(2L));
        itemRepository.saveAndFlush(ItemGenerator.getItem(1L));
        userRepository.saveAndFlush(UserGenerator.getUser(3L));
        userRepository.saveAndFlush(UserGenerator.getUser(4L));
        itemRepository.saveAndFlush(ItemGenerator.getItem(2L));
        BookingDto bookingDto = bookingService.createBooking(2L, BookingGenerator.getCreateBookingDto(1L));
        assertEquals(1L, bookingDto.getId());
        List<BookingDto> bookingDtoList = bookingService.getBookingAll(2L, State.ALL.name(), true);
        assertEquals(0, bookingDtoList.size());
        bookingDtoList = bookingService.getBookingAll(2L, State.ALL.name(), true, 0, 10);
        assertEquals(0, bookingDtoList.size());
        bookingDtoList = bookingService.getBookingAll(2L, State.ALL.name(), false);
        assertEquals(1, bookingDtoList.size());
        bookingDtoList = bookingService.getBookingAll(2L, State.ALL.name(), false, 0, 10);
        assertEquals(1, bookingDtoList.size());

        bookingDto = bookingService.createBooking(4L, BookingGenerator.getCreateBookingDto(2L));
        assertEquals(2L, bookingDto.getId());
        bookingDtoList = bookingService.getBookingAll(4L, State.CURRENT.name(), false);
        assertEquals(0, bookingDtoList.size());
        bookingDtoList = bookingService.getBookingAll(4L, State.FUTURE.name(), false);
        assertEquals(1, bookingDtoList.size());
        bookingDtoList = bookingService.getBookingAll(4L, State.PAST.name(), false);
        assertEquals(0, bookingDtoList.size());
        bookingDtoList = bookingService.getBookingAll(4L, State.WAITING.name(), false);
        assertEquals(1, bookingDtoList.size());
        bookingDtoList = bookingService.getBookingAll(4L, State.REJECTED.name(), false);
        assertEquals(0, bookingDtoList.size());
    }

}
