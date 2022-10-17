package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;


public interface BookingService {
    BookingDto createBooking(Long userId, CreateBookingDto createBookingDto);

    BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingAll(Long userId, String state, Boolean owner);

    List<BookingDto> getBookingAll(Long userId, String state, Boolean owner, Integer from, Integer size);
}
