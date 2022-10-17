package ru.practicum.shareit.booking;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.utils.BookingGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        bookingController = new BookingController(bookingService);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void createBookingOk() {
        when(bookingService.createBooking(anyLong(), any(CreateBookingDto.class))).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return BookingGenerator.getBookingDto(id);
        });
        BookingDto bookingDto = bookingController.createBooking(1L, BookingGenerator.getCreateBookingDto(1L));
        assertEquals(1L, bookingDto.getId());
        verify(bookingService).createBooking(anyLong(), any(CreateBookingDto.class));
    }

    @Test
    void approvedBookingOk() {
        when(bookingService.approvedBooking(anyLong(), anyLong(), anyBoolean())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(1);
            return BookingGenerator.getBookingDto(id);
        });
        BookingDto bookingDto = bookingController.approvedBooking(1L, 1L, Boolean.TRUE);
        assertEquals(1L, bookingDto.getId());
        verify(bookingService).approvedBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingByIdOk() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(1);
            return BookingGenerator.getBookingDto(id);
        });
        BookingDto bookingDto = bookingController.getBookingById(1L, 1L);
        assertEquals(1L, bookingDto.getId());
        verify(bookingService).getBookingById(anyLong(), anyLong());
    }

    @Test
    void getBookingAllOk() {
        when(bookingService.getBookingAll(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt())).thenReturn(BookingGenerator.getBookingsDto(5));
        List<BookingDto> bookingDtoList = bookingController.getBookingAll(1L, "All", 0, 10);
        assertEquals(5, bookingDtoList.size());
        verify(bookingService).getBookingAll(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getBookingAllFail() {
        when(bookingService.getBookingAll(anyLong(), anyString(), anyBoolean())).thenReturn(BookingGenerator.getBookingsDto(4));
        assertThrows(ValidationException.class, () -> bookingController.getBookingAll(1L, "All", -10, 10));
        assertThrows(ValidationException.class, () -> bookingController.getBookingAll(1L, "All", 0, -10));
        List<BookingDto> bookingDtoList = bookingController.getBookingAll(1L, "All", null, 10);
        assertEquals(4, bookingDtoList.size());
        bookingDtoList = bookingController.getBookingAll(1L, "All", 0, null);
        assertEquals(4, bookingDtoList.size());
        verify(bookingService, times(2)).getBookingAll(anyLong(), anyString(), anyBoolean());

    }

    @Test
    void getBookingOwnerAllOk() {
        when(bookingService.getBookingAll(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt())).thenReturn(BookingGenerator.getBookingsDto(5));
        List<BookingDto> bookingDtoList = bookingController.getBookingOwnerAll(1L, "All", 0, 10);
        assertEquals(5, bookingDtoList.size());
        verify(bookingService).getBookingAll(anyLong(), anyString(), anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getBookingOwnerAllFail() {
        when(bookingService.getBookingAll(anyLong(), anyString(), anyBoolean())).thenReturn(BookingGenerator.getBookingsDto(4));
        assertThrows(ValidationException.class, () -> bookingController.getBookingOwnerAll(1L, "All", -10, 10));
        assertThrows(ValidationException.class, () -> bookingController.getBookingOwnerAll(1L, "All", 0, -10));
        List<BookingDto> bookingDtoList = bookingController.getBookingOwnerAll(1L, "All", null, 10);
        assertEquals(4, bookingDtoList.size());
        bookingDtoList = bookingController.getBookingOwnerAll(1L, "All", 0, null);
        assertEquals(4, bookingDtoList.size());
        verify(bookingService, times(2)).getBookingAll(anyLong(), anyString(), anyBoolean());

    }

}