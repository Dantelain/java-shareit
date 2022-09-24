package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;


public interface BookingService {
    CreateBookingDto createBooking(Long userId, CreateBookingDto createBookingDto);

    ResponseBookingDto approvedBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingAll(Long userId, String state, Boolean owner);
}
